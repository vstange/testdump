package de.vstange.runner;

import com.formulasearchengine.mathmlconverters.canonicalize.MathMLCanUtil;
import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import de.vstange.entity.*;
import de.vstange.query.QueryCollect;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test class to reproduce previous research results
 * regarding the math similarity factors.
 *
 * @author Vincent Stange
 */
@Component
public class TestRunner implements CommandLineRunner {

    public final static String RESULT_TABLE_NAME = "results_new";

    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private VotesRepository votesRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final Log logger = LogFactory.getLog(getClass());

    private List<Exception> errorlist = new ArrayList<>();

    @Override
    public void run(String... strings) throws Exception {
        deleteResults();
        compareRun();
    }

    public void compareRun() throws IOException {
        List<Query> queries = prepareQueries(prepareQueries());
        // filter for certain queries (optional)
//        queries = queries.stream()
//                .filter(q -> q.getQueryNum() == 33 && Objects.equals(q.getQueryFormulaId(), "f1.0"))
//                .collect(Collectors.toList());

        List<Formula> formulas = getFormulas();

        MultiValueMap<String, Integer> voteFilterMap = getFilterMap();

        logger.info("begin streaming over formulas");
        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0L);

        formulas.forEach(f -> {
            long time = System.currentTimeMillis();

            compareFormula(f, queries, voteFilterMap);

            // compute avg time and print out
            long tookTime = System.currentTimeMillis() - time;
            long avgTime = totalTime.accumulateAndGet(tookTime, (l, r) -> l + r) / counter.incrementAndGet();
            logger.info("=== time (ms): " + tookTime + " (avg: " + avgTime + ")  " + counter.get() + " | " + f.getName());
            f = null; // explicitly declare null for gc
        });
        logger.info("finished (total: " + totalTime.get() + ")");

        // print every collected error at the end
        errorlist.forEach(Throwable::printStackTrace);
    }

    /**
     * Get all the votes from the database and order them in entries like:
     * formula_name=[list of query nums]
     *
     * @return map
     */
    private MultiValueMap<String, Integer> getFilterMap() {
        logger.info("collect and prepare votes");
        MultiValueMap<String, Integer> voteMap = new LinkedMultiValueMap<>(2500);
        votesRepository.findAll().forEach(v -> {
            voteMap.add(v.getPageId(), v.getqId());
        });
        long count = voteMap.values().stream().mapToLong(Collection::size).sum();
        logger.info("votes collected");
        return voteMap;
    }

    /**
     * Compare specific formula against all used queries.
     *
     * @param formula specific formula
     * @param queries used queries
     * @param voteMap filter map entry: formula_name=[list of query nums]
     * @return list of all results
     */
    private List<Result> compareFormula(Formula formula, List<Query> queries, MultiValueMap<String, Integer> voteMap) {
        // preparations
        Timestamp timestamp = new Timestamp(new Date().getTime());
        try {
            // canonicalize
            formula.setValue(MathMLCanUtil.canonicalize(formula.getValue()));
            // prepare cached objects
            formula.setCmmlInfo(new CMMLInfo(formula.getValue()));
            formula.setAbstractCmmlInfo(new CMMLInfo(formula.getValue()).toStrictCmml().abstract2CDs());
            formula.setDataCmmlInfo(new CMMLInfo(formula.getValue()).toStrictCmml().abstract2DTs());
        } catch (Exception e) {
            logger.error("could not build CMMLInfo for the formula: " + formula.getName(), e);
            errorlist.add(e);
            return Collections.emptyList();
        }

        // comparison of one formula against all queries in parallel
        List<Result> resultSet = Collections.synchronizedList(new ArrayList<>(queries.size()));
        queries.parallelStream()
                .filter(q -> voteMap.getOrDefault(formula.getSectionname(), Collections.EMPTY_LIST).contains(q.getQueryNum()))
                .forEach(query -> {

                    Map<String, Object> oldFactors = compareOriginalFactors(query, formula);
                    try {
                        Double coverage = (Double) oldFactors.get("coverage");
                        Integer depth = (Integer) oldFactors.get("depth");
                        Boolean structureMatch = (Boolean) oldFactors.get("structure match");
                        Boolean dataMatch = (Boolean) oldFactors.get("data match");
                        Boolean isFormula = (Boolean) oldFactors.get("equation");

                        Result result = new Result();
                        result.setQuerynum(query.getQueryNum());
                        result.setQueryformulaid(query.getQueryFormulaId());
                        result.setPatternname(formula.getName());
                        result.setPageid(formula.getSectionname());
                        result.setCdMatch(structureMatch == null ? 0 : structureMatch ? 1 : 0);
                        result.setDataMatch(dataMatch == null ? 0 : dataMatch ? 1 : 0);
                        result.setQueryCoverage(coverage);
                        result.setMatchDepth(depth);
                        result.setIsFormulae(isFormula == null ? 0 : isFormula ? 1 : 0);
                        result.setTimestamp(timestamp);

                        resultSet.add(result);
                    } catch (Exception e) {
                        logger.error("result mapping for DB object failed", e);
                    }
                });

        // clean up / free space - explicitly declare null for gc
        formula.setCmmlInfo(null);
        formula.setAbstractCmmlInfo(null);
        formula.setDataCmmlInfo(null);

        // save and return
        resultRepository.save(resultSet);
        return resultSet;
    }

    /**
     * Original similarity factors.
     *
     * @param query   Reference MathML string (must contain pMML and cMML)
     * @param formula Comparison MathML string (must contain pMML and cMML)
     * @return map of all similarity factors
     */
    private Map<String, Object> compareOriginalFactors(Query query, Formula formula) {
        try {
            CMMLInfo compDoc = formula.getCmmlInfo();

            final Integer depth = compDoc.getDepth(query.getQuery());
            final Double coverage = compDoc.getCoverage(query.getCmmlInfo().getElements(true));
            Boolean equation = compDoc.isEquation(true);
            Boolean match = formula.getAbstractCmmlInfo().isMatch(query.getAbstractQuery());
            Boolean dataMatch = formula.getDataCmmlInfo().isMatch(query.getDataQuery());

            HashMap<String, Object> result = new HashMap<>();
            result.put("depth", depth);
            result.put("coverage", coverage);
            result.put("structure match", match);
            result.put("data match", dataMatch);
            result.put("equation", equation);
            return result;
        } catch (Exception e) {
            logger.error(formula.getName() + " : " + query.getQueryNum(), e);
            return null;
        }
    }

    /**
     * Prepare queries means caching the CMMLInfo objects for
     * different match-methods.
     *
     * @param queries list of all used queries
     * @return list of all used queries
     */
    private List<Query> prepareQueries(List<Query> queries) {
        logger.info("collect and prepare queries");
        // cache the document objects of each query
        queries.parallelStream().forEach(q -> {
            try {
                // canonicalize
//                q.setMathml(MathMLCanUtil.canonicalize(q.getMathml())); // mathml can
                // prepare cached objects
                CMMLInfo queryDoc = new CMMLInfo(q.getMathml());
                q.setCmmlInfo(queryDoc);
                try {
                    q.setQuery(queryDoc.getXQuery());
                } catch (StackOverflowError e) {
                    // TODO version fuck up
                    logger.error(q.getQueryFormulaId() + ", " + q.getQueryNum() + ", " + q.getMathml(), e);
                    return;
//                    System.exit(5);

                }
                q.setAbstractQuery(new CMMLInfo(q.getMathml()).toStrictCmml().abstract2CDs().getXQuery());
                q.setDataQuery(new CMMLInfo(q.getMathml()).toStrictCmml().abstract2DTs().getXQuery());
            } catch (Exception e) {
                logger.error("could not build query: " + q.getQueryNum(), e);
                errorlist.add(e);
            }
        });

        queries.removeIf(q -> q.getAbstractQuery() == null);

        logger.info("queries collected");
        return queries;
    }

    /**
     * Get all formulas used for the comparison.
     *
     * @return list of all used formulas
     */
    private List<Formula> getFormulas() {
        logger.info("collect and prepare formulas");
//        List<Formula> formulas = Collections.singletonList(formulaRepository.findByNameAndSectionname("S2.p11.1.2.m13.1", "math0609061_1_12"));
        List<Formula> formulas = formulaRepository.findAll();
        logger.info("formulas collected");
        return formulas;
    }

    /**
     * Get queries used for the comparison.
     *
     * @return list of all used queries
     * @throws IOException could not read the resource file
     */
    private List<Query> prepareQueries() throws IOException {
        // Read all queries from the file within our resource folder
        String s = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fQuery.xml"), "UTF-8");
        return new QueryCollect().getQueries(s);
    }

    /**
     * Delete all previous results in the result table.
     */
    private void deleteResults() {
        logger.info("delete results");
        jdbcTemplate.execute("truncate " + RESULT_TABLE_NAME);
        logger.info("results deleted");
    }
}

package de.vstange.controller;

import com.formulasearchengine.mathmlsim.similarity.util.MathMLCanUtil;
import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import de.vstange.entity.*;
import de.vstange.query.QueryCollect;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private ResultRepository resultRepository;

    private final Log logger = LogFactory.getLog(getClass());

    private List<Exception> errorlist = new ArrayList<>();

    @Override
    public void run(String... strings) throws Exception {
        deleteResults();
        compareRun();
    }

    public void compareRun() throws IOException {
        logger.info("collect and prepare queries");
        List<Query> queries = prepareQueries(getQueries());
        logger.info("queries collected");

        logger.info("collect and prepare formulas");
        List<Formula> formulas = formulaRepository.findAll();
        // math ml canonicalization for all formulas
        formulas.forEach(f -> {
            try {
                f.setValue(MathMLCanUtil.canonicalize(f.getValue()));
            } catch (Exception ignore) {
            }
        });
        // filter for certain formulas (optional)
//        formulas = formulas.stream()
//                .filter(f -> f.getName().equals("S7.Ex1.m1.1"))
//                .filter(f -> f.getSectionname().equals("math0506287_1_147"))
//                .collect(Collectors.toList());
        logger.info("formulas collected");

        logger.info("begin streaming over all formulas");
        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0L);
        formulas.forEach(f -> {
            long time = System.currentTimeMillis();

            compareFormula(f, queries);
            // compute avg time and print out
            long tookTime = System.currentTimeMillis() - time;
            long avgTime = totalTime.accumulateAndGet(tookTime, (l, r) -> l + r) / counter.incrementAndGet();
            logger.info("=== time (ms): " + tookTime + " (avg: " + avgTime + ")  " + counter.get() + " | " + f.getName());
            f = null; // explicitly declare null for gc
        });
        logger.info("finished (total: " + totalTime.get() + ")");

        // print errors
        errorlist.forEach(Throwable::printStackTrace);
    }

    private List<Query> prepareQueries(List<Query> queries) {
        // cache the document objects of each query
        queries.parallelStream().forEach(q -> {
            try {
                q.setMathml(MathMLCanUtil.canonicalize(q.getMathml())); // mathml can
                CMMLInfo queryDoc = new CMMLInfo(q.getMathml());
                q.setCmmlInfo(queryDoc);
                q.setQuery(queryDoc.getXQuery());
                q.setAbstractQuery(new CMMLInfo(q.getMathml()).toStrictCmml().abstract2CDs().getXQuery());
                q.setDataQuery(new CMMLInfo(q.getMathml()).toStrictCmml().abstract2DTs().getXQuery());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return queries;
    }

    private void compareFormula(Formula formula, List<Query> queries) {
        // prep work
        try {
            formula.setCmmlInfo(new CMMLInfo(formula.getValue()));
            formula.setAbstractCmmlInfo(new CMMLInfo(formula.getValue()).toStrictCmml().abstract2CDs());
            formula.setDataCmmlInfo(new CMMLInfo(formula.getValue()).toStrictCmml().abstract2DTs());
        } catch (Exception e) {
            e.printStackTrace();
            errorlist.add(e);
            return;
        }

        List<Result> resultSet = Collections.synchronizedList(new ArrayList<>(queries.size()));
        queries.parallelStream().forEach(query -> {
            Map<String, Object> oldFactors = compareOriginalFactors(query, formula);
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
            result.setMatchDepth(depth == null ? -1 : depth);
            result.setIsFormulae(isFormula == null ? 0 : isFormula ? 1 : 0);

            result.setTimestamp(new Timestamp(new Date().getTime()));

            resultSet.add(result);
        });
        resultRepository.save(resultSet);

        // clean up / free space - explicitly declare null for gc
        formula.setCmmlInfo(null);
        formula.setAbstractCmmlInfo(null);
        formula.setDataCmmlInfo(null);
        resultSet.clear();
    }

    /**
     * Old score - this is basically a print out for comparison and be deleted later on
     *
     * @param query   Reference MathML string (must contain pMML and cMML)
     * @param formula Comparison MathML string (must contain pMML and cMML)
     * @return ap of all found factors
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

    private List<Query> getQueries() throws IOException {
        // Read all queries from the file within our resource folder
        String s = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fQuery.xml"), "UTF-8");
        return new QueryCollect().getQueries(s);
    }

    private void deleteResults() {
        logger.info("delete results");
        resultRepository.deleteAll();
        logger.info("results deleted");
    }
}

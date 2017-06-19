package de.vstange.controller;

import com.formulasearchengine.mathmlsim.similarity.MathPlag;
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
 * Test class for reproducing
 *
 * @author Vincent Stange
 */
@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private ResultRepository resultRepository;

    private final Log logger = LogFactory.getLog(getClass());

    private List<Exception> errorlist = new ArrayList<>();

    @Override
    public void run(String... strings) throws Exception {
//        qvar_single_test();

//        testRun();

        deleteResults();
        compareRun();
    }

    public void compareRun() throws IOException {
        logger.info("collecting and preparing queries");
        List<Query> queries = getQueries();
        queries.parallelStream().forEach(q -> {
            try {
                CMMLInfo queryDoc = new CMMLInfo(q.getMathml());
                q.setCmmlInfo(queryDoc);
                q.setQuery(queryDoc.getXQuery());
                q.setAbstractQuery(new CMMLInfo(q.getMathml()).toStrictCmml().abstract2CDs().getXQuery());
                q.setDataQuery(new CMMLInfo(q.getMathml()).toStrictCmml().abstract2DTs().getXQuery());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        logger.info("queries collected");

        logger.info("collecting formulas");
        List<Formula> formulas = formulaRepository.findAll();
        // filtering
//        formulas = formulas.stream()
//                .filter(f -> f.getName().equals("S7.Ex1.m1.1"))
//                .filter(f -> f.getSectionname().equals("math0506287_1_147"))
//                .collect(Collectors.toList());
        logger.info("formulas collected");

        logger.info("begin streaming");
        AtomicInteger counter = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0L);
        formulas.forEach(f -> {
            long time = System.currentTimeMillis();

            compareFormula(f, queries);
            // compute avg time and print out
            long tookTime = System.currentTimeMillis() - time;
            long avgTime = totalTime.accumulateAndGet(tookTime, (l, r) -> l + r) / counter.incrementAndGet();
            System.out.println("=== time (ms): " + tookTime + " (avg: " + avgTime + ")  " + counter.get() + " | " + f.getName());
            f = null;
        });
        logger.info("finished (total: " + totalTime.get() + ")");

        // print errors
        errorlist.forEach(Throwable::printStackTrace);
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

        // clean up - free space
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
            System.err.println(formula.getName() + " : " + query.getQueryNum());
            e.printStackTrace();
            return null;
        }
    }

    private Result createRecord(Result result) {
        return resultRepository.save(result);
    }

    private void testRun() throws IOException {
        logger.info("Formulas: " + formulaRepository.count());
        logger.info("Results: " + resultRepository.count());
        logger.info("queries: " + getQueries());
    }

    private List<Query> getQueries() throws IOException {
        String s = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("fQuery.xml"), "UTF-8");
        return new QueryCollect().getQueries(s);
    }

    private void deleteResults() {
        logger.info("delete results");
        resultRepository.deleteAll();
        logger.info("results deleted");
    }


    public void qvar_single_test() {
        String math1 = "<m:math xmlns:m=\"http://www.w3.org/1998/Math/MathML\">\n" +
                "                    <m:semantics xml:id=\"m1.1a\" xref=\"m1.1.pmml\">\n" +
                "                        <m:apply xml:id=\"m1.1.8\" xref=\"m1.1.8.pmml\">\n" +
                "                            <m:eq xml:id=\"m1.1.5\" xref=\"m1.1.5.pmml\"/>\n" +
                "                            <m:apply xml:id=\"m1.1.8.1\" xref=\"m1.1.8.1.pmml\">\n" +
                "                                <m:times xml:id=\"m1.1.8.1.1\" xref=\"m1.1.8.1.1.pmml\"/>\n" +
                "                                <mws:qvar xmlns:mws=\"http://search.mathweb.org/ns\" name=\"square\"/>\n" +
                "                                <mws:qvar xmlns:mws=\"http://search.mathweb.org/ns\" name=\"phi\"/>\n" +
                "                            </m:apply>\n" +
                "                            <m:apply xml:id=\"m1.1.8.2\" xref=\"m1.1.8.2.pmml\">\n" +
                "                                <m:times xml:id=\"m1.1.8.2.1\" xref=\"m1.1.8.2.1.pmml\"/>\n" +
                "                                <m:ci xml:id=\"m1.1.6\" xref=\"m1.1.6.pmml\">i</m:ci>\n" +
                "                                <m:ci xml:id=\"m1.1.7\" xref=\"m1.1.7.pmml\">d</m:ci>\n" +
                "                            </m:apply>\n" +
                "                        </m:apply>\n" +
                "                        <m:annotation-xml encoding=\"MathML-Presentation\" xml:id=\"m1.1.pmml\" xref=\"m1.1\">\n" +
                "                            <m:mrow xml:id=\"m1.1.8.pmml\" xref=\"m1.1.8\">\n" +
                "                                <m:mrow xml:id=\"m1.1.8.1.pmml\" xref=\"m1.1.8.1\">\n" +
                "                                    <mws:qvar xmlns:mws=\"http://search.mathweb.org/ns\" name=\"\uD835\uDC60\uD835\uDC5E\uD835\uDC62\uD835\uDC4E\uD835\uDC5F\uD835\uDC52\"/>\n" +
                "                                    <m:mo xml:id=\"m1.1.8.1.1.pmml\" xref=\"m1.1.8.1.1\">\u2062</m:mo>\n" +
                "                                    <m:mrow xml:id=\"m1.1.3.pmml\" xref=\"m1.1.3\">\n" +
                "                                        <m:mo xml:id=\"m1.1.3a.pmml\" xref=\"m1.1.3\">(</m:mo>\n" +
                "                                        <mws:qvar xmlns:mws=\"http://search.mathweb.org/ns\" name=\"\uD835\uDC5Dℎ\uD835\uDC56\"/>\n" +
                "                                        <m:mo xml:id=\"m1.1.3c.pmml\" xref=\"m1.1.3\">)</m:mo>\n" +
                "                                    </m:mrow>\n" +
                "                                </m:mrow>\n" +
                "                                <m:mo xml:id=\"m1.1.5.pmml\" xref=\"m1.1.5\">=</m:mo>\n" +
                "                                <m:mrow xml:id=\"m1.1.8.2.pmml\" xref=\"m1.1.8.2\">\n" +
                "                                    <m:mi xml:id=\"m1.1.6.pmml\" xref=\"m1.1.6\">i</m:mi>\n" +
                "                                    <m:mo xml:id=\"m1.1.8.2.1.pmml\" xref=\"m1.1.8.2.1\">\u2062</m:mo>\n" +
                "                                    <m:mi xml:id=\"m1.1.7.pmml\" xref=\"m1.1.7\">d</m:mi>\n" +
                "                                </m:mrow>\n" +
                "                            </m:mrow>\n" +
                "                        </m:annotation-xml>\n" +
                "                        <m:annotation encoding=\"application/x-tex\" xml:id=\"m1.1b\" xref=\"m1.1.pmml\">\n" +
                "                            \\qvar{square}(\\qvar{phi})=id\n" +
                "                        </m:annotation>\n" +
                "                    </m:semantics>\n" +
                "                </m:math>";

        String math2 = "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"\n" +
                "      alttext=\"F(R)=\\rho(S)\"\n" +
                "      class=\"ltx_Math\"\n" +
                "      display=\"inline\"\n" +
                "      id=\"Thmdefinition1.p1.1.1.m8.1\"\n" +
                "      xref=\"Thmdefinition1.p1.1.1.m8.1.cmml\">\n" +
                "   <semantics id=\"Thmdefinition1.p1.1.1.m8.1a\" xref=\"Thmdefinition1.p1.1.1.m8.1.cmml\">\n" +
                "      <mrow id=\"Thmdefinition1.p1.1.1.m8.1.10\"\n" +
                "            xref=\"Thmdefinition1.p1.1.1.m8.1.10.cmml\">\n" +
                "         <mrow id=\"Thmdefinition1.p1.1.1.m8.1.10.1\"\n" +
                "               xref=\"Thmdefinition1.p1.1.1.m8.1.10.1.cmml\">\n" +
                "            <mi id=\"Thmdefinition1.p1.1.1.m8.1.1\"\n" +
                "                xref=\"Thmdefinition1.p1.1.1.m8.1.1.cmml\">F</mi>\n" +
                "            <mo id=\"Thmdefinition1.p1.1.1.m8.1.10.1.1\"\n" +
                "                mathvariant=\"italic\"\n" +
                "                xref=\"Thmdefinition1.p1.1.1.m8.1.10.1.1.cmml\">?</mo>\n" +
                "            <mrow id=\"Thmdefinition1.p1.1.1.m8.1.3\"\n" +
                "                  xref=\"Thmdefinition1.p1.1.1.m8.1.3.cmml\">\n" +
                "               <mo id=\"Thmdefinition1.p1.1.1.m8.1.3a\"\n" +
                "                   mathvariant=\"italic\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.3.cmml\">(</mo>\n" +
                "               <mi id=\"Thmdefinition1.p1.1.1.m8.1.3b\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.3.cmml\">R</mi>\n" +
                "               <mo id=\"Thmdefinition1.p1.1.1.m8.1.3c\"\n" +
                "                   mathvariant=\"italic\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.3.cmml\">)</mo>\n" +
                "            </mrow>\n" +
                "         </mrow>\n" +
                "         <mo id=\"Thmdefinition1.p1.1.1.m8.1.5\"\n" +
                "             mathvariant=\"normal\"\n" +
                "             xref=\"Thmdefinition1.p1.1.1.m8.1.5.cmml\">=</mo>\n" +
                "         <mrow id=\"Thmdefinition1.p1.1.1.m8.1.10.2\"\n" +
                "               xref=\"Thmdefinition1.p1.1.1.m8.1.10.2.cmml\">\n" +
                "            <mi id=\"Thmdefinition1.p1.1.1.m8.1.6\"\n" +
                "                xref=\"Thmdefinition1.p1.1.1.m8.1.6.cmml\">?</mi>\n" +
                "            <mo id=\"Thmdefinition1.p1.1.1.m8.1.10.2.1\"\n" +
                "                mathvariant=\"italic\"\n" +
                "                xref=\"Thmdefinition1.p1.1.1.m8.1.10.2.1.cmml\">?</mo>\n" +
                "            <mrow id=\"Thmdefinition1.p1.1.1.m8.1.8\"\n" +
                "                  xref=\"Thmdefinition1.p1.1.1.m8.1.8.cmml\">\n" +
                "               <mo id=\"Thmdefinition1.p1.1.1.m8.1.8a\"\n" +
                "                   mathvariant=\"italic\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.8.cmml\">(</mo>\n" +
                "               <mi id=\"Thmdefinition1.p1.1.1.m8.1.8b\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.8.cmml\">S</mi>\n" +
                "               <mo id=\"Thmdefinition1.p1.1.1.m8.1.8c\"\n" +
                "                   mathvariant=\"italic\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.8.cmml\">)</mo>\n" +
                "            </mrow>\n" +
                "         </mrow>\n" +
                "      </mrow>\n" +
                "      <annotation-xml encoding=\"MathML-Content\"\n" +
                "                      id=\"Thmdefinition1.p1.1.1.m8.1.cmml\"\n" +
                "                      xref=\"Thmdefinition1.p1.1.1.m8.1\">\n" +
                "         <apply id=\"Thmdefinition1.p1.1.1.m8.1.10.cmml\"\n" +
                "                xref=\"Thmdefinition1.p1.1.1.m8.1.10\">\n" +
                "            <eq id=\"Thmdefinition1.p1.1.1.m8.1.5.cmml\"\n" +
                "                xref=\"Thmdefinition1.p1.1.1.m8.1.5\"/>\n" +
                "            <apply id=\"Thmdefinition1.p1.1.1.m8.1.10.1.cmml\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.10.1\">\n" +
                "               <times id=\"Thmdefinition1.p1.1.1.m8.1.10.1.1.cmml\"\n" +
                "                      xref=\"Thmdefinition1.p1.1.1.m8.1.10.1.1\"/>\n" +
                "               <ci id=\"Thmdefinition1.p1.1.1.m8.1.1.cmml\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.1\">F</ci>\n" +
                "               <ci id=\"Thmdefinition1.p1.1.1.m8.1.3.cmml\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.3\">R</ci>\n" +
                "            </apply>\n" +
                "            <apply id=\"Thmdefinition1.p1.1.1.m8.1.10.2.cmml\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.10.2\">\n" +
                "               <times id=\"Thmdefinition1.p1.1.1.m8.1.10.2.1.cmml\"\n" +
                "                      xref=\"Thmdefinition1.p1.1.1.m8.1.10.2.1\"/>\n" +
                "               <ci id=\"Thmdefinition1.p1.1.1.m8.1.6.cmml\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.6\">?</ci>\n" +
                "               <ci id=\"Thmdefinition1.p1.1.1.m8.1.8.cmml\"\n" +
                "                   xref=\"Thmdefinition1.p1.1.1.m8.1.8\">S</ci>\n" +
                "            </apply>\n" +
                "         </apply>\n" +
                "      </annotation-xml>\n" +
                "      <annotation encoding=\"application/x-tex\"\n" +
                "                  id=\"Thmdefinition1.p1.1.1.m8.1b\"\n" +
                "                  xref=\"Thmdefinition1.p1.1.1.m8.1.cmml\">F(R)=\\rho(S)</annotation>\n" +
                "   </semantics>\n" +
                "</math>";

        Map<String, Object> stringObjectMap = MathPlag.compareOriginalFactors(math1, math2);
        System.out.println(stringObjectMap);
    }
}

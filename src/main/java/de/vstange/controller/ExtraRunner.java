package de.vstange.controller;

import com.formulasearchengine.mathmlsim.similarity.MathPlag;
import de.vstange.entity.FormulaRepository;
import de.vstange.entity.ResultRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

/**
 * Just some test additional functions - to try things out.
 *
 * @author Vincent Stange
 */
public class ExtraRunner {

    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private ResultRepository resultRepository;

    private final Log logger = LogFactory.getLog(getClass());

    public void run(String... strings) throws Exception {
        countingDB();
        qvar_single_test();
    }

    private void countingDB() throws IOException {
        logger.info("Formulas: " + formulaRepository.count());
        logger.info("Results: " + resultRepository.count());
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

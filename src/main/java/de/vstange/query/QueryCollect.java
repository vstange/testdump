package de.vstange.query;

import com.formulasearchengine.mathmlsim.similarity.util.XMLUtils;
import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import com.formulasearchengine.mathmltools.xmlhelper.XMLHelper;
import de.vstange.entity.Query;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Vincent Stange
 */
public class QueryCollect {

    private static final String START = "<topic>";

    private static final String END = "</topic>";

    List<String> topics = new ArrayList<>();

    List<Query> queries = new ArrayList<>();

    private XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);

    public QueryCollect() {
        topics.clear();
    }

    public List<String> collectTopics(String textinput) {
        int startIdx = textinput.indexOf(START);
        for (; startIdx != -1; startIdx = textinput.indexOf(START, startIdx + 1)) {
            int endIdx = textinput.indexOf(END, startIdx + 1);

            if (endIdx != -1) {
                String topic = textinput.substring(startIdx, endIdx + END.length());
                topic = topic.replace("<topic>", //"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<topic xmlns:m=\"http://www.w3.org/1998/Math/MathML\">");
                topics.add(topic);
            } else
                throw new RuntimeException("missing end-tag");

        }
        return topics;
    }

    public List<Query> getQueries(String textinput) {
        collectTopics(textinput);

        for (String topic : topics) {
            Document document = XMLHelper.string2Doc(topic, false);
            String topicNum = getTopicNum(document);
            NodeList formulas = getFormulas(document);
            for (int i = 0; i < formulas.getLength(); i++) {
                Element formulaNode = (Element) formulas.item(i);
                String attrId = formulaNode.getAttribute("id");
                try {
                    Node mathChild = getMath(formulaNode);
                    String mathml = XMLUtils.nodeToString(mathChild).trim();

                    // clean up mathml
//                    mathml = mathml.replaceAll("xml:", "");
//                    mathml = mathml.replaceAll("m:", "");
//                    mathml = mathml.replaceAll("xmlns:m", "xmlns");

                    Query query = new Query();
                    query.setQueryNum(Integer.valueOf(topicNum.split("-")[2]));
                    query.setQueryFormulaId(attrId);
                    query.setMathml(mathml);
                    queries.add(query);
                } catch (Exception e) {
                    throw new RuntimeException("blub", e);
                }
            }

        }
        return queries;
    }

    public String getTopicNum(Document document) {
        try {
            return (String) xpath.compile("/topic/num").evaluate(document, XPathConstants.STRING);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public NodeList getFormulas(Document document) {
        try {
            return (NodeList) xpath.compile("//formula").evaluate(document, XPathConstants.NODESET);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Node getMath(Node formula) {
        try {
            return (Node) xpath.compile("//m:math").evaluate(formula, XPathConstants.NODE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

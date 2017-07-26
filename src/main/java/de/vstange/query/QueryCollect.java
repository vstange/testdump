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
 * Collects all queries from the specified XML document.
 *
 * @author Vincent Stange
 */
public class QueryCollect {

    private static final String START = "<topic>";

    private static final String END = "</topic>";

    private List<String> topics = new ArrayList<>();

    private List<Query> queries = new ArrayList<>();

    private XPath xpath = XMLHelper.namespaceAwareXpath("m", CMMLInfo.NS_MATHML);

    /**
     * To avoid namespace handling and to make it a bit more efficient, this is a linear
     * text search for topics inside a provided XML document.
     *
     * @param xmlInput specific query document as a String
     * @return List of all topics as Strings
     */
    private List<String> collectTopics(String xmlInput) {
        topics.clear();
        int startIdx = xmlInput.indexOf(START);
        for (; startIdx != -1; startIdx = xmlInput.indexOf(START, startIdx + 1)) {
            int endIdx = xmlInput.indexOf(END, startIdx + 1);

            if (endIdx != -1) {
                String topic = xmlInput.substring(startIdx, endIdx + END.length());
                topic = topic.replace("<topic>", //"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<topic xmlns:m=\"http://www.w3.org/1998/Math/MathML\">");
                topics.add(topic);
            } else
                throw new RuntimeException("missing end-tag");

        }
        return topics;
    }

    /**
     * Get all queries contained inside the specified XML document.
     *
     * @param xmlInput specific query document as a String
     * @return list of all queries
     */
    public List<Query> getQueries(String xmlInput) {
        // clear previous collection
        queries.clear();
        collectTopics(xmlInput);
        // each topic can contain multiple queries
        for (String topic : topics) {
            Document document = XMLHelper.string2Doc(topic, false);
            String topicNum = getTopicNum(document);
            NodeList formulas = getFormulas(document);
            for (int i = 0; i < formulas.getLength(); i++) {
                Element formulaNode = (Element) formulas.item(i);
                String attrId = formulaNode.getAttribute("id");
                try {
                    Node mathChild = getMath(formulaNode);
                    String mathml = XMLUtils.nodeToString(mathChild, true).trim();
                    // set each one up as a query object
                    Query query = new Query();
                    query.setQueryNum(Integer.valueOf(topicNum.split("-")[2]));
                    query.setQueryFormulaId(attrId);
                    query.setMathml(mathml);
                    queries.add(query);
                } catch (Exception e) {
                    throw new RuntimeException("xml to string transformation failed", e);
                }
            }
        }
        return queries;
    }

    private String getTopicNum(Document document) {
        try {
            return (String) xpath.compile("/topic/num").evaluate(document, XPathConstants.STRING);
        } catch (Exception e) {
            throw new RuntimeException("xpath extraction failed", e);
        }
    }

    private NodeList getFormulas(Document document) {
        try {
            return (NodeList) xpath.compile("//formula").evaluate(document, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new RuntimeException("xpath extraction failed", e);
        }
    }

    private Node getMath(Node formulaNode) {
        try {
            return (Node) xpath.evaluate("./*[1]", formulaNode, XPathConstants.NODE);
        } catch (Exception e) {
            throw new RuntimeException("xpath extraction failed", e);
        }
    }
}
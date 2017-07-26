package de.vstange.entity;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import net.sf.saxon.s9api.XQueryExecutable;

/**
 * Hibernate wrapper for the result table entries.
 *
 * @author Vincent Stange
 */
public class Query {

    private Integer queryNum;

    private String queryFormulaId;

    private String mathml;

    private CMMLInfo cmmlInfo;

    /**
     * cached query of the unchanged document
     */
    private XQueryExecutable query;

    /**
     * cached query of the strict document
     */
    private XQueryExecutable abstractQuery;

    /**
     * cached query of the data abstract document
     */
    private XQueryExecutable dataQuery;


    public Integer getQueryNum() {
        return queryNum;
    }

    public void setQueryNum(Integer queryNum) {
        this.queryNum = queryNum;
    }

    public String getQueryFormulaId() {
        return queryFormulaId;
    }

    public void setQueryFormulaId(String queryFormulaId) {
        this.queryFormulaId = queryFormulaId;
    }

    public String getMathml() {
        return mathml;
    }

    public void setMathml(String mathml) {
        this.mathml = mathml;
    }

    public XQueryExecutable getQuery() {
        return query;
    }

    public void setQuery(XQueryExecutable query) {
        this.query = query;
    }

    public XQueryExecutable getAbstractQuery() {
        return abstractQuery;
    }

    public void setAbstractQuery(XQueryExecutable abstractQuery) {
        this.abstractQuery = abstractQuery;
    }

    public CMMLInfo getCmmlInfo() {
        return cmmlInfo;
    }

    public void setCmmlInfo(CMMLInfo cmmlInfo) {
        this.cmmlInfo = cmmlInfo;
    }

    public void setDataQuery(XQueryExecutable dataQuery) {
        this.dataQuery = dataQuery;
    }

    public XQueryExecutable getDataQuery() {
        return dataQuery;
    }
}

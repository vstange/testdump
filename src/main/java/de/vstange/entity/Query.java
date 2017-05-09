package de.vstange.entity;

/**
 * TODO
 *
 * @author Vincent Stange
 */
public class Query {

    private Integer queryNum;

    private String queryFormulaId;

    private String mathml;

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
}

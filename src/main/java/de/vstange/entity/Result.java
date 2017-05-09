package de.vstange.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * TODO
 *
 * @author Vincent Stange
 */
@Entity
@Table(name = "results",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"querynum", "queryformulaid", "patternname", "pageid"})
        })
@IdClass(ResuldId.class)
public class Result {

    @Id
    @Column(name = "querynum")
    private Integer querynum;

    @Id
    @Column(name = "queryformulaid")
    private String queryformulaid;

    @Id
    @Column(name = "patternname")
    private String patternname;

    @Id
    @Column(name = "pageid")
    private String pageid;

    @Column(name = "cdmatch")
    private Integer cdMatch;

    @Column(name = "datamatch")
    private Integer dataMatch;

    @Column(name = "matchdepth")
    private Integer matchDepth;

    @Column(name = "querycoverage")
    private Double queryCoverage;

    @Column(name = "isformulae")
    private Integer isFormulae;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "vote")
    private Integer vote;

    @Column(name = "covcat")
    private Integer covCat;

    public Integer getQuerynum() {
        return querynum;
    }

    public void setQuerynum(Integer querynum) {
        this.querynum = querynum;
    }

    public String getQueryformulaid() {
        return queryformulaid;
    }

    public void setQueryformulaid(String queryformulaid) {
        this.queryformulaid = queryformulaid;
    }

    public String getPatternname() {
        return patternname;
    }

    public void setPatternname(String patternname) {
        this.patternname = patternname;
    }

    public Integer getCdMatch() {
        return cdMatch;
    }

    public void setCdMatch(Integer cdMatch) {
        this.cdMatch = cdMatch;
    }

    public Integer getDataMatch() {
        return dataMatch;
    }

    public void setDataMatch(Integer dataMatch) {
        this.dataMatch = dataMatch;
    }

    public Integer getMatchDepth() {
        return matchDepth;
    }

    public void setMatchDepth(Integer matchDepth) {
        this.matchDepth = matchDepth;
    }

    public Double getQueryCoverage() {
        return queryCoverage;
    }

    public void setQueryCoverage(Double queryCoverage) {
        this.queryCoverage = queryCoverage;
    }

    public Integer getIsFormulae() {
        return isFormulae;
    }

    public void setIsFormulae(Integer isFormulae) {
        this.isFormulae = isFormulae;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }

    public String getPageid() {
        return pageid;
    }

    public void setPageid(String pageid) {
        this.pageid = pageid;
    }

    public Integer getCovCat() {
        return covCat;
    }

    public void setCovCat(Integer covCat) {
        this.covCat = covCat;
    }
}

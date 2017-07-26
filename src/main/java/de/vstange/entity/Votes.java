package de.vstange.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Hibernate wrapper for the vote table entries.
 *
 * @author Vincent Stange
 */
@Entity
@Table(name = "referee_votes")
public class Votes {

    @Column(name = "qid", nullable = false)
    private Integer qId;

    @Id
    @Column(name = "pageid", nullable = false)
    private String pageId;

    @Column(name = "vote")
    private Integer vote;

    public Integer getqId() {
        return qId;
    }

    public void setqId(Integer qId) {
        this.qId = qId;
    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public Integer getVote() {
        return vote;
    }

    public void setVote(Integer vote) {
        this.vote = vote;
    }
}
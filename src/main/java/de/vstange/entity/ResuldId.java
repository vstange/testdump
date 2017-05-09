package de.vstange.entity;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * TODO
 *
 * @author Vincent Stange
 */
public class ResuldId implements Serializable {

    @Column(name = "")
    private Integer querynum;
    private String queryformulaid;
    private String patternname;
    private String pageid;
}

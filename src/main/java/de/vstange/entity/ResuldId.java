package de.vstange.entity;

import java.io.Serializable;

/**
 * Separate class to declare for hibernate a unique Id for
 * the result table.
 *
 * @author Vincent Stange
 */
public class ResuldId implements Serializable {

    private Integer querynum;
    private String queryformulaid;
    private String patternname;
    private String pageid;
}
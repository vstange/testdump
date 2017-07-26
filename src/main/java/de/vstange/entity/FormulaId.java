package de.vstange.entity;

import java.io.Serializable;

/**
 * Separate class to declare for hibernate a unique Id for
 * the formula table.
 *
 * @author Vincent Stange
 */
public class FormulaId implements Serializable {
    private String name;
    private String sectionname;
}

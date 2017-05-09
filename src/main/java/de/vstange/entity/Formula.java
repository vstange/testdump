package de.vstange.entity;

import javax.persistence.*;

/**
 * TODO
 *
 * @author Vincent Stange
 */
@Entity
@Table(name = "formulae_fulltext",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"formula_name", "sectionname"})
        })
@IdClass(FormulaId.class)
public class Formula {


    @Id
    @Column(name = "formula_name", nullable = false)
    private String name;

    @Id
    @Column(name = "sectionname", nullable = false)
    private String sectionname;

    @Column(name = "value")
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSectionname() {
        return sectionname;
    }

    public void setSectionname(String sectionname) {
        this.sectionname = sectionname;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

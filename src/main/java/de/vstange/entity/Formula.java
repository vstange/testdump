package de.vstange.entity;

import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import net.sf.saxon.s9api.XQueryExecutable;

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

    @Transient
    private CMMLInfo cmmlInfo;

    @Transient
    private CMMLInfo abstractCmmlInfo;

    @Transient
    private CMMLInfo dataCmmlInfo;

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

    public CMMLInfo getCmmlInfo() {
        return cmmlInfo;
    }

    public void setCmmlInfo(CMMLInfo cmmlInfo) {
        this.cmmlInfo = cmmlInfo;
    }

    public CMMLInfo getAbstractCmmlInfo() {
        return abstractCmmlInfo;
    }

    public void setAbstractCmmlInfo(CMMLInfo abstractCmmlInfo) {
        this.abstractCmmlInfo = abstractCmmlInfo;
    }

    public void setDataCmmlInfo(CMMLInfo dataCmmlInfo) {
        this.dataCmmlInfo = dataCmmlInfo;
    }

    public CMMLInfo getDataCmmlInfo() {
        return dataCmmlInfo;
    }
}

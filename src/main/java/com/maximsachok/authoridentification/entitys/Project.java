package com.maximsachok.authoridentification.entitys;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="t_project",schema = "semantic")
public class Project {
    @Id
    @Column(name="project_id_tk")
    private BigInteger projectIdTk;

    @Column(name="id_t_project_program")
    private BigInteger idProjectProgram;

    @Column(name="project_id_bk")
    private String idBk;

    @Column(name="project_number")
    private String number;

    @Column(name="project_name_orig")
    private String nameOrig;

    @Column(name="project_name_en")
    private String nameEn;

    @Column(name="project_desc_orig")
    private String descorig;

    @Column(name="project_desc_en")
    private String descEn;

    @Column(name="project_keywords")
    private String keywords;

    @Column(name="project_start_date")
    private Date startDate;

    @Column(name="project_end_date")
    private Date endDate;

    @Column(name="project_start_year")
    private int startYear;

    @Column(name="project_end_year")
    private int endYear;

    @Column(name="project_state")
    private String state;

    @Column(name="project_type")
    private String type;

    public BigInteger getProjectIdTk() {
        return projectIdTk;
    }

    public void setProjectIdTk(BigInteger projectIdTk) {
        this.projectIdTk = projectIdTk;
    }

    public BigInteger getIdProjectProgram() {
        return idProjectProgram;
    }

    public void setIdProjectProgram(BigInteger idProjectProgram) {
        this.idProjectProgram = idProjectProgram;
    }

    public String getIdBk() {
        return idBk;
    }

    public void setIdBk(String idBk) {
        this.idBk = idBk;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getNameOrig() {
        return nameOrig;
    }

    public void setNameOrig(String nameOrig) {
        this.nameOrig = nameOrig;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getDescorig() {
        return descorig;
    }

    public void setDescorig(String descorig) {
        this.descorig = descorig;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }
}

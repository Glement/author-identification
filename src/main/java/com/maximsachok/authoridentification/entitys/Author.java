package com.maximsachok.authoridentification.entitys;



import javax.persistence.*;
import javax.validation.constraints.Size;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "t_expert", schema = "semantic", indexes = @Index(name = "t_expert_expert_id_tk_idx", columnList = "expert_id_tk", unique = true))
public class Author {
    @Id
    @Column(name = "expert_id_tk")
    private BigInteger expertidtk;

    @Column(name = "expert_bk")
    private String expertbk;

    @Column(name = "expert_wordvec")
    private String expertWordVec;

    @Column(name = "expert_tf")
    private String expertTf;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "surname")
    private String surname;

    @Column(name = "inicials")
    private String inicials;

    @Column(name = "email")
    private String email;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "vedidk")
    private BigInteger vedidk;

    @Column(name = "state")
    @Size(min=0,max=50)
    private String state;

    @Column(name="source_system")
    private String sourceSystem;

    @Column(name = "stercore")
    @Size(min=0,max=5)
    private String stercore;

    @Column(name = "orcid")
    private String orcid;

    @Column(name = "scopusid")
    private String scopusid;

    @Column(name = "researcherid")
    private String researcherid;

    @Column(name = "unicoid")
    private String unicoid;

    @Column(name="claim_bk")
    private String claimBk;

    public String getExpertWordVec() {
        return expertWordVec;
    }

    public void setExpertWordVec(String expertWordVec) {
        this.expertWordVec = expertWordVec;
    }

    public String getExpertTf() {
        return expertTf;
    }

    public void setExpertTf(String expertTf) {
        this.expertTf = expertTf;
    }

    public BigInteger getExpertidtk() {
        return expertidtk;
    }

    public void setExpertidtk(BigInteger expertidtk) {
        this.expertidtk = expertidtk;
    }

    public String getExpertbk() {
        return expertbk;
    }

    public void setExpertbk(String expertbk) {
        this.expertbk = expertbk;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getInicials() {
        return inicials;
    }

    public void setInicials(String inicials) {
        this.inicials = inicials;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public BigInteger getVedidk() {
        return vedidk;
    }

    public void setVedidk(BigInteger vedidk) {
        this.vedidk = vedidk;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getStercore() {
        return stercore;
    }

    public void setStercore(String stercore) {
        this.stercore = stercore;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getScopusid() {
        return scopusid;
    }

    public void setScopusid(String scopusid) {
        this.scopusid = scopusid;
    }

    public String getResearcherid() {
        return researcherid;
    }

    public void setResearcherid(String researcherid) {
        this.researcherid = researcherid;
    }

    public String getUnicoid() {
        return unicoid;
    }

    public void setUnicoid(String unicoid) {
        this.unicoid = unicoid;
    }

    public String getClaimBk() {
        return claimBk;
    }

    public void setClaimBk(String claimBk) {
        this.claimBk = claimBk;
    }
}

package com.maximsachok.authoridentification.entitys;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "t_expert_t_project_rel", schema = "semantic")
@IdClass(AuthorProjectCompositeId.class)
public class AuthorProject {
    @Id
    @Column(name = "expert_id_tk_t_expert")
    private BigInteger expertId;

    @Id
    @Column(name = "project_id_tk_t_project")
    private BigInteger projectId;

    @Column(name = "project_id_bk")
    private String projectIdBk;

    @Column(name = "expert_bk")
    private String expertBk;

    @Column(name = "role")
    private String role;


    public BigInteger getExpertId() {
        return expertId;
    }

    public void setExpertId(BigInteger expertId) {
        this.expertId = expertId;
    }

    public BigInteger getProjectId() {
        return projectId;
    }

    public void setProjectId(BigInteger projectId) {
        this.projectId = projectId;
    }

    public String getProjectIdBk() {
        return projectIdBk;
    }

    public void setProjectIdBk(String projectIdBk) {
        this.projectIdBk = projectIdBk;
    }

    public String getExpertBk() {
        return expertBk;
    }

    public void setExpertBk(String expertBk) {
        this.expertBk = expertBk;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

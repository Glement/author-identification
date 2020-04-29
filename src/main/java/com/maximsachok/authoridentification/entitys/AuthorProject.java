package com.maximsachok.authoridentification.entitys;

import javax.persistence.*;

@Entity
@Table(name = "t_expert_t_project_rel", schema = "semantic")
@IdClass(AuthorProjectCompositeId.class)
public class AuthorProject {
    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expert_id_tk_t_expert", referencedColumnName = "expert_id_tk")
    private Author author;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id_tk_t_project", referencedColumnName = "project_id_tk")
    private Project project;

    @Column(name = "project_id_bk")
    private String projectIdBk;

    @Column(name = "expert_bk")
    private String expertBk;

    @Column(name = "role")
    private String role;


    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
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

package com.maximsachok.authoridentification.entitys;


import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="t_project")
public class Project {
    @Id
    @Column(name="project_id_tk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  projectIdTk;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private Set<AuthorProject> authorProjects;


    @Column(name="project_name_en", columnDefinition="TEXT")
    private String nameEn;

    @Column(name="project_desc_en", columnDefinition="TEXT")
    private String descEn;

    @Column(name="project_keywords", columnDefinition="TEXT")
    private String keywords;

    public Long getProjectIdTk() {
        return projectIdTk;
    }

    public void setProjectIdTk(Long projectIdTk) {
        this.projectIdTk = projectIdTk;
    }

    public Set<AuthorProject> getAuthorProjects() {
        return authorProjects;
    }

    public void setAuthorProjects(Set<AuthorProject> authorProjects) {
        this.authorProjects = authorProjects;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
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

    public String asString() {
        return nameEn+" "+keywords+" "+descEn;
    }
}

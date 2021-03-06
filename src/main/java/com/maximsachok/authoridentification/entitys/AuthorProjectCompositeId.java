package com.maximsachok.authoridentification.entitys;


import java.io.Serializable;
import java.util.Objects;

public class AuthorProjectCompositeId implements Serializable {
    private Long author;

    private Long project;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorProjectCompositeId that = (AuthorProjectCompositeId) o;
        return Objects.equals(author, that.author) &&
                Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, project);
    }

    public Long getAuthor() {
        return author;
    }

    public void setAuthor(Long author) {
        this.author = author;
    }

    public Long getProject() {
        return project;
    }

    public void setProject(Long project) {
        this.project = project;
    }
}

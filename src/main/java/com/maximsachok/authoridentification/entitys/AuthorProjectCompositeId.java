package com.maximsachok.authoridentification.entitys;


import java.io.Serializable;
import java.util.Objects;

public class AuthorProjectCompositeId implements Serializable {
    private Author author;

    private Project project;

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
}

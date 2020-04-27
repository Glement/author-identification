package com.maximsachok.authoridentification.entitys;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

public class AuthorProjectCompositeId implements Serializable {
    private BigInteger expertId;

    private BigInteger projectId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthorProjectCompositeId that = (AuthorProjectCompositeId) o;
        return Objects.equals(expertId, that.expertId) &&
                Objects.equals(projectId, that.projectId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expertId, projectId);
    }
}

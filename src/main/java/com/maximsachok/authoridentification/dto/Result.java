package com.maximsachok.authoridentification.dto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * Class containing author id, and author similarity scores (bayes score and TF-IDF score).
 * @see com.maximsachok.authoridentification.textvectorization.TextVectorization#calculateTfIdfForAuthor(List)
 */
public class Result implements Serializable {
    private Double tfScore;
    private Double bayesScore;
    private long authorID;

    public Double getBayesScore() {
        return bayesScore;
    }

    public void setBayesScore(Double bayesScore) {
        this.bayesScore = bayesScore;
    }

    public Double getTfScore() {
        return tfScore;
    }

    public void setTfScore(Double tfScore) {
        this.tfScore = tfScore;
    }

    public long getAuthorID() {
        return authorID;
    }

    public void setAuthorID(long authorID) {
        this.authorID = authorID;
    }

    @Override
    public String toString() {
        return "Result{" +
                "tfScore=" + tfScore +
                ", bayesScore=" + bayesScore +
                ", authorID=" + authorID +
                '}';
    }
}

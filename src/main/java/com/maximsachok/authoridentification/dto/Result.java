package com.maximsachok.authoridentification.dto;

import java.io.Serializable;
import java.math.BigInteger;

public class Result implements Serializable {
    private Double wordScore;
    private Double tfScore;
    private BigInteger authorID;

    public Double getWordScore() {
        return wordScore;
    }

    public void setWordScore(Double wordScore) {
        this.wordScore = wordScore;
    }

    public Double getTfScore() {
        return tfScore;
    }

    public void setTfScore(Double tfScore) {
        this.tfScore = tfScore;
    }

    public BigInteger getAuthorID() {
        return authorID;
    }

    public void setAuthorID(BigInteger authorID) {
        this.authorID = authorID;
    }

    @Override
    public String toString() {
        return "Result{" +
                "wordScore=" + wordScore +
                ", tfScore=" + tfScore +
                ", authorID=" + authorID +
                '}';
    }
}

package com.maximsachok.authoridentification.dto;

import java.util.ArrayList;

/**
 * Response class returned to user.
 * Contain lists of Result sorted by tfScore and bayesScore.
 * Sorted in descending order. The lower the score, the lower the possibility that this author is the author of given project.
 */
public class Response {
    private ArrayList<Result> tfTop = null;
    private ArrayList<Result> bayesTop = null;

    public ArrayList<Result> getTfTop() {
        return tfTop;
    }

    public void setTfTop(ArrayList<Result> tfTop) {
        this.tfTop = tfTop;
    }

    public ArrayList<Result> getBayesTop() {
        return bayesTop;
    }

    public void setBayesTop(ArrayList<Result> bothTop) {
        this.bayesTop = bothTop;
    }
}

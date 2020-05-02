package com.maximsachok.authoridentification.dto;

import java.util.ArrayList;

/**
 * Response class returned to user.
 * Contain lists of
 * @see Result
 * sorted by
 * @see Result#getTfScore()
 * or
 * @see Result#getWordScore()
 *or both of them with the priorioty of
 * @see Result#getWordScore().
 * Sorted in descending orde. The lower the score, the lower the possibility that this author is the author of given project.
 */
public class Response {
    private ArrayList<Result> wordTop = null;
    private ArrayList<Result> tfTop = null;
    private ArrayList<Result> bothTop = null;

    public ArrayList<Result> getWordTop() {
        return wordTop;
    }

    public void setWordTop(ArrayList<Result> wordTop) {
        this.wordTop = wordTop;
    }

    public ArrayList<Result> getTfTop() {
        return tfTop;
    }

    public void setTfTop(ArrayList<Result> tfTop) {
        this.tfTop = tfTop;
    }

    public ArrayList<Result> getBothTop() {
        return bothTop;
    }

    public void setBothTop(ArrayList<Result> bothTop) {
        this.bothTop = bothTop;
    }
}

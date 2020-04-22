package com.maximsachok.authoridentification.dto;

import java.util.ArrayList;

public class Response {
    ArrayList<Result> wordTop = null;
    ArrayList<Result> tfTop = null;
    ArrayList<Result> bothTop = null;

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

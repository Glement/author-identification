package com.maximsachok.authoridentification.textvectorization;

import java.util.Arrays;

public class WordVector {
    private double[] vector;

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    @Override
    public String toString() {
        return "WordVector{" +
                "vector=" + Arrays.toString(vector) +
                '}';
    }
}

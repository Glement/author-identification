package com.maximsachok.authoridentification.textvectorization;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity {
    public double compareTwoVectors(double[] first, double[]second) {
        if(first.length != second.length)
            return -10;
        double dotProduct = 0.0d;
        double d1 = 0.0d;
        double d2 = 0.0d;
        for(int i=0; i<first.length;++i){
            d1 += Math.pow(first[i],2);
            d2 += Math.pow(second[i],2);
            dotProduct += first[i]*second[i];
        }
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
        } else {
            cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
        }
        return cosineSimilarity;
    }

    public double compareTwoMaps(final Map<String,Double> leftVector,final  Map<String,Double> rightVector) {

        final Set<String> intersection = getIntersection(leftVector, rightVector);
        final double dotProduct = dot(leftVector, rightVector, intersection);
        double d1 = 0.0d;
        for (final Double value : leftVector.values()) {
            d1 += Math.pow(value, 2);
        }
        double d2 = 0.0d;
        for (final Double value : rightVector.values()) {
            d2 += Math.pow(value, 2);
        }
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
            } else {
            cosineSimilarity = dotProduct / (Math.sqrt(d1) * Math.sqrt(d2));
            }
        return cosineSimilarity;
    }

    private Set<String> getIntersection(final Map<String, Double> leftVector, final Map<String, Double> rightVector) {
        final Set<String> intersection = new HashSet<>(leftVector.keySet());
        intersection.retainAll(rightVector.keySet());
        return intersection;
    }

    private double dot(final Map<String, Double> leftVector, final Map<String, Double> rightVector,
                       final Set<String> intersection) {
        double dotProduct = 0;
        for (final String key : intersection) {
            dotProduct += leftVector.get(key) * rightVector.get(key);
        }
        return dotProduct;
    }
}

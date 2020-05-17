package com.maximsachok.authoridentification.textvectorization;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class that implements cosine similarity between two vectors.
 * When the result of similarity is between -1 and 1.
 * Where 1 means Vectors are identical and -1 means vectors are opposite to each other.
 */
public class CosineSimilarity {

    /**
     * Compares two maps by taking the intersection of them.
     * @param first First map
     * @param second Second map
     * @return Returns the cosine similarity between those maps, if maps do not have any common keys, returned value is -1.
     */
    public double compareTwoMaps(final Map<String,Double> first,final  Map<String,Double> second) {

        final Set<String> intersection = getIntersection(first, second);
        if(intersection.isEmpty())
            return -1.0d;
        final double dotProduct = dot(first, second, intersection);
        double d1 = 0.0d;
        for (final Double value : first.values()) {
            d1 += Math.pow(value, 2);
        }
        double d2 = 0.0d;
        for (final Double value : second.values()) {
            d2 += Math.pow(value, 2);
        }
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = -1.0d;
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
        double dotProduct = 0.0d;
        for (final String key : intersection) {
            dotProduct += leftVector.get(key) * rightVector.get(key);
        }
        return dotProduct;
    }
}

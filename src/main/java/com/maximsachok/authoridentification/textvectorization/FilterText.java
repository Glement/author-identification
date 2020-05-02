package com.maximsachok.authoridentification.textvectorization;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Filters given string, removing all except for letters, apostrophes and spaces(maximum of 1 consecutive spaces).
 * Removes Stop Words which does not contain any meaning and are useless for purposes of this application.
 */
public class FilterText {
    /**
     * Filters text
     * @param text Input text
     * @return Text converted to lower case, which contains only letters, apostrophes and spaces.
     */
    public static Collection<String> filter(String text)
    {
        String inputText = text.trim().toLowerCase().replaceAll("[^a-z ']", " ").replaceAll(" +", " ");
//Collect all tokens into labels collection.
        Collection<String> labels = Arrays.asList(inputText.split(" ")).parallelStream().filter(label->label.length()>0).collect(Collectors.toList());
//get from standard text files available for Stopwords. e.g https://algs4.cs.princeton.edu/35applications/stopwords.txt
        labels = labels.parallelStream().filter(label ->  !StopWords.getStopWords().contains(label.trim())).collect(Collectors.toList());
        return labels;
    }
}

package com.maximsachok.authoridentification.textvectorization;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class FilterText {
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

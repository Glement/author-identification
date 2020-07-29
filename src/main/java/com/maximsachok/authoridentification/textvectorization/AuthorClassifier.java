package com.maximsachok.authoridentification.textvectorization;

import com.maximsachok.authoridentification.entitys.Author;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.List;
public interface AuthorClassifier {
    double testClassifier();
    void initClassifier(List<Author> authors);
    List<ImmutablePair<Double,String>> classifyText(String text);
    void resetClassifier();
}

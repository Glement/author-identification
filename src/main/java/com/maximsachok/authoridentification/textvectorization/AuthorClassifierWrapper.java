package com.maximsachok.authoridentification.textvectorization;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.SearchResultDto;
import com.maximsachok.authoridentification.entitys.Author;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface AuthorClassifierWrapper {
    Boolean isInitialized();

    Boolean isInitializing();

    Boolean isRefreshRequested();

    double testAlgorithm();

    void initClassifier(List<Author> allAuthors, AuthorClassifier authorClassifier);
    
    List<SearchResultDto> findPossibleAuthor(ProjectDto project);

    void refreshClassifier(List<Author> allAuthors);
}

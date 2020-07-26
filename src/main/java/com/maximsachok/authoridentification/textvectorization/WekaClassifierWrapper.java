package com.maximsachok.authoridentification.textvectorization;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.SearchResultDto;
import com.maximsachok.authoridentification.entitys.Author;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jboss.logging.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WekaClassifierWrapper implements AuthorClassifierWrapper{
    private final AtomicBoolean classifierIsInitialized = new AtomicBoolean(false);
    private final AtomicBoolean classifierIsInitializing = new AtomicBoolean(false);
    private final AtomicBoolean classifierRefreshIsRequested = new AtomicBoolean(false);
    private final AtomicInteger numberOfThreadsUsingClassifier = new AtomicInteger(0);
    private AuthorClassifier authorClassifier;
    public WekaClassifierWrapper(){
    }


    @Override
    public Boolean isInitialized() {
        return classifierIsInitialized.get();
    }

    @Override
    public Boolean isInitializing() {
        return classifierIsInitializing.get();
    }

    @Override
    public Boolean isRefreshRequested() {
        return classifierRefreshIsRequested.get();
    }

    public double testAlgorithm(){
        if(!classifierIsInitialized.get())
            return 0;
        numberOfThreadsUsingClassifier.incrementAndGet();
        double result = authorClassifier.testClassifier();
        numberOfThreadsUsingClassifier.decrementAndGet();
        return result;
    }

    public void initClassifier(List<Author> allAuthors, AuthorClassifier authorClassifier){
        if(classifierIsInitializing.get())
            return;
        classifierIsInitialized.set(false);
        classifierIsInitializing.set(true);
        this.authorClassifier = authorClassifier;
        this.authorClassifier.initClassifier(allAuthors);
        classifierIsInitializing.set(false);
        classifierIsInitialized.set(true);
    }

    /**
     * Looks through all authors and finds possible author that could have written given project, if author already have this project it is excluded from search.
     * @param project Project for which to find possible author
     * @return List of  ids of possible authors of given text, default number is 10
     */
    public List<SearchResultDto> findPossibleAuthor(ProjectDto project){
        if(classifierIsInitializing.get() || classifierRefreshIsRequested.get())
            return new ArrayList<>();
        numberOfThreadsUsingClassifier.incrementAndGet();
        List<SearchResultDto> result = new ArrayList<>();
        for(ImmutablePair<Double, String> pair : authorClassifier.classifyText(project.asString())){
            AuthorDto authorDto = new AuthorDto();
            authorDto.setId(Long.decode(pair.getValue()));
            result.add(new SearchResultDto(authorDto, pair.getKey()));
        }

        if(numberOfThreadsUsingClassifier.decrementAndGet()==0 && classifierRefreshIsRequested.get()){
            synchronized (numberOfThreadsUsingClassifier){
                numberOfThreadsUsingClassifier.notify();
            }
        }
        return result;
    }

    public void refreshClassifier(List<Author> allAuthors){
        if(classifierRefreshIsRequested.get() || classifierIsInitializing.get())
            return;
        classifierRefreshIsRequested.set(true);
        synchronized (numberOfThreadsUsingClassifier){
            try {
                if(numberOfThreadsUsingClassifier.get()>0)
                    numberOfThreadsUsingClassifier.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            authorClassifier.resetClassifier();
            authorClassifier.initClassifier(allAuthors);
            classifierRefreshIsRequested.set(false);
        }
    }


}

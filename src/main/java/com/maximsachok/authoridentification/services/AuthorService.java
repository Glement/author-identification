package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.textcomparation.FindPossibleTextClass;
import com.maximsachok.authoridentification.textvectorization.TestClassifier;
import com.maximsachok.authoridentification.textvectorization.TextClassifier;
import com.maximsachok.authoridentification.utils.DivideList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Starts the n threads which are looking for a possible author of project, n depends on authorList size.
     * @param authorList List of List of Author
     * @param project String project
     * @return
     */
    private List<Author> findAuthors(List<List<Author>> authorList, String project){
        List<Author> result = new ArrayList<>();
        List<Future<Author>> threadList = new ArrayList<>();
        Author futureResult = null;
        try {

            for(List<Author> authors : authorList){
                threadList.add(Executors.newCachedThreadPool().submit(new FindPossibleTextClass(authors, project)));
            }
            for(Future<Author> future : threadList){
                futureResult = future.get();
                if(futureResult!=null){
                    result.add(futureResult);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public double testAlgorithm(){
        TestClassifier testClassifier = new TestClassifier();
        return testClassifier.testAlgorithm(authorRepository.findAll());
    }


    /**
     * Looks through all authors and finds possible author that could have written given project, if author already have this project it is excluded from search.
     * @param project Project for which to find possible author
     * @return Long id of possible author of given text
     */
    public Long findPossibleAuthor(ProjectDto project) throws Exception {

        Long authorID = null;
        List<Author> allAuthors = authorRepository.findAll();
        List<Author> secondaryCheckAuthors = new ArrayList<>();
        List<Author> foundAuthors;
        while(!allAuthors.isEmpty()){
            for(List<List<Author>> listOfAuthorLists : DivideList.batchList(DivideList.batchList(allAuthors,2),12)){
                foundAuthors = findAuthors(listOfAuthorLists,project.asString());
                if(foundAuthors!=null)
                    secondaryCheckAuthors.addAll(foundAuthors);
            }
            if(secondaryCheckAuthors.size()==1){
                authorID = secondaryCheckAuthors.get(0).getExpertidtk();
                break;
            }
            allAuthors.clear();
            allAuthors.addAll(secondaryCheckAuthors);
            secondaryCheckAuthors.clear();
        }
        return authorID;
    }






}

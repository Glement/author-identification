package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.textvectorization.TextClassifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.bayes.NaiveBayesMultinomialText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AuthorService {

    private AuthorRepository authorRepository;
    private static Boolean updating = false;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Looks through all authors and finds possible author that could have written given project, if author already have this project it is excluded from search.
     * @param project Project for which to find possible author
     * @return Long id of possible author of given text
     */
    public Long findPossibleAuthor(ProjectDto project) throws Exception {
        double[] classifierResult;
        TextClassifier textClassifier;
        boolean doNotCompare;
        textClassifier = TextClassifier.getTextClassifier();
        if(!textClassifier.isInitialized()) {
            updateAllAuthors();
        }
        textClassifier = TextClassifier.getTextClassifier();
        assert textClassifier != null;
        classifierResult = textClassifier.classifyMessage(project.asString());
        Long authorID = null;
        double maxScore = 0.0d;
        for(Author author : authorRepository.findAll())
        {
            doNotCompare = false;
            for(Project project1 : getProjects(author)){
                if(project1.asString().equals(project.asString())){
                    doNotCompare = true;
                    break;
                }
            }
            if(doNotCompare)
                continue;
            if(classifierResult!=null && textClassifier.getClassIndex(author.getExpertidtk().toString())>-1 &&
                    textClassifier.getClassIndex(author.getExpertidtk().toString())<classifierResult.length){
                if(maxScore<(classifierResult[textClassifier.getClassIndex(author.getExpertidtk().toString())])){
                    maxScore = (classifierResult[textClassifier.getClassIndex(author.getExpertidtk().toString())]);
                    authorID = author.getExpertidtk();
                }
            }
        }
        return authorID;
    }

    /**
     * Function to check the accuracy of chosen classifier.
     * @return Double value as number of correct guesses divided by total amount.
     */
    public double testAlgorithm(){
        double numberOfAuthors=0;
        double numberOfFoundBayes=0;

        List<Author> authorList = authorRepository.findAll();

        Map<Author,List<Project>> map = new HashMap<>();

        Map<Author,Project> deleted = new HashMap<>();

        boolean skipProject;
        for(Author author : authorList){
            List<Project> projects = getProjects(author);
            if(projects.size()>=2){
                deleted.put(author,projects.get(0));
                projects.remove(0);
                map.put(author,projects);
                numberOfAuthors++;
            }
            if(map.size()>50){
                break;
            }
        }
        TextClassifier.updateClassifier();
        TextClassifier textClassifier = TextClassifier.getTextClassifier();
        for(Author author : map.keySet()){
            textClassifier.addCategory(author.getExpertidtk().toString());
        }
        textClassifier.setupAfterCategorysAdded();
        for(Author author : map.keySet()){
            for(Project project: map.get(author)){
                textClassifier.addData(project.asString(),author.getExpertidtk().toString());
            }

        }
        System.out.println("Started building");
        try{
            textClassifier.buildIfNeeded();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.println("Finish building");
        for(Author author : deleted.keySet()){
            System.out.println("Going through authors collecting score");

            double [] classifierResult = null;
            try{
                classifierResult = textClassifier.classifyMessage(deleted.get(author).asString());
            }
            catch (Exception ex){
                System.out.println("Error when classifying message");
                ex.printStackTrace();
            }
            Long authorID = null;
            double maxScore = 0.0d;
            for(Author author2 : map.keySet()) {
                skipProject = false;
                for(Project project: map.get(author2)){
                    if(deleted.get(author).asString().equals(project.asString())){
                        skipProject=true;
                        break;
                    }
                }
                if(skipProject)
                    continue;
                if(classifierResult!=null && textClassifier.getClassIndex(author2.getExpertidtk().toString())>-1 &&
                        textClassifier.getClassIndex(author2.getExpertidtk().toString())<classifierResult.length){
                    if(maxScore<(classifierResult[textClassifier.getClassIndex(author2.getExpertidtk().toString())])){
                        maxScore = (classifierResult[textClassifier.getClassIndex(author2.getExpertidtk().toString())]);
                        authorID = author2.getExpertidtk();
                    }
                }
            }
            if(authorID!=null && authorID.equals(author.getExpertidtk())){
                numberOfFoundBayes++;
            }

        }
        System.out.println("Algorithm accuracy is "+(numberOfFoundBayes/numberOfAuthors)*100+"%");

        return numberOfFoundBayes/numberOfAuthors;
    }

    private List<Project> getProjects(Author author){
        List<Project> projects = new ArrayList<>();
        for(AuthorProject authorProject : author.getAuthorProjects()){
            projects.add(authorProject.getProject());
        }
        return projects;
    }

    private void updateClassifier(List<Author> authors){
        TextClassifier textClassifier = TextClassifier.updateClassifier();
        for(Author author : authors){
            List<Project> projects;
            projects = getProjects(author);
            if(projects.isEmpty())
                continue;
            textClassifier.addCategory(author.getExpertidtk().toString());
        }
        textClassifier.setupAfterCategorysAdded();
        for(Author author : authors){
            List<Project> projects;
            projects = getProjects(author);
            if(projects.isEmpty())
                continue;
            for(Project project: projects){
                textClassifier.addData(project.asString(),author.getExpertidtk().toString());
            }
        }
        try{
            textClassifier.buildIfNeeded();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }

    /**
     * Updates classifier collecting all authors.
     */
    public void updateAllAuthors()  {
        if(updating)
            return;
        AuthorService.updating = true;
        List<Author> authors = authorRepository.findAll();
        updateClassifier(authors);
        AuthorService.updating = false;
    }



    public Boolean isUpdating(){
        return updating;
    }

}

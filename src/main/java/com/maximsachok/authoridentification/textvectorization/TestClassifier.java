package com.maximsachok.authoridentification.textvectorization;

import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.textcomparation.FindPossibleTextClass;
import com.maximsachok.authoridentification.utils.DivideList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class for testing the chosen classifier.
 */
public class TestClassifier {

    private List<Project> getProjects(Author author){
        List<Project> projects = new ArrayList<>();
        for(AuthorProject authorProject : author.getAuthorProjects()){
            projects.add(authorProject.getProject());
        }
        return projects;
    }

    private List<Author> findAuthors(List<List<Author>> authorList, String project, Map<Author, List<Project>> map){
        List<Author> result = new ArrayList<>();
        List<Future<Author>> threadList = new ArrayList<>();
        Author futureResult;
        try {

            for(List<Author> authors : authorList){
                threadList.add(Executors.newCachedThreadPool().submit(new FindPossibleTextClassForTesting(authors, project, map)));
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

    /**
     * Function to check the accuracy of a chosen classifier. Takes all authors with more than 2 projects and strips 1 out. Then checks if classifier can find the author of a stripped project.
     * @return double value as number of correct guesses divided by total amount.
     */
    public double testAlgorithm(List<Author> authorList){
        double numberOfAuthors=0;
        double numberOfFound=0;
        double attemptNumber = 0;

        Map<Author,List<Project>> map = new HashMap<>();

        Map<Author,Project> deleted = new HashMap<>();

        List<Author> authors = new ArrayList<>();
        for(Author author : authorList){
            List<Project> projects = getProjects(author);
            if(projects.size()>=2){
                deleted.put(author,projects.get(0));
                projects.remove(0);
                map.put(author,projects);
                numberOfAuthors++;
                authors.add(author);
            }
        }
        for(Author author : deleted.keySet()){
            Long authorID = null;
            List<Author> allAuthors = new ArrayList<>(authors);
            List<Author> secondaryCheckAuthors = new ArrayList<>();
            List<Author> foundAuthors;
            boolean notFound;
            while(!allAuthors.isEmpty()){
                notFound = false;
                for(List<List<Author>> listOfAuthorLists : DivideList.batchList(DivideList.batchList(allAuthors,2),12)){
                    foundAuthors = findAuthors(listOfAuthorLists,deleted.get(author).asString(), map);
                    if(foundAuthors!=null)
                        secondaryCheckAuthors.addAll(foundAuthors);
                }

                if(secondaryCheckAuthors.size()==1){
                    System.out.println("Found authorID="+secondaryCheckAuthors.get(0).getExpertidtk()+" but should be authorID="+author.getExpertidtk());
                    authorID = secondaryCheckAuthors.get(0).getExpertidtk();
                    break;
                }
                allAuthors.clear();
                allAuthors.addAll(secondaryCheckAuthors);
                secondaryCheckAuthors.clear();
            }
            if(authorID!=null && authorID.equals(author.getExpertidtk())){
                numberOfFound++;
            }
            attemptNumber++;
            System.out.println("Number of found  "+numberOfFound+" and current number of attempts is "+attemptNumber+" out of "+numberOfAuthors+" total");
        }
        System.out.println("Algorithm accuracy is "+(numberOfFound/numberOfAuthors)*100+"%");

        return numberOfFound/numberOfAuthors;
    }

    private  TextClassifier testBuildClassifier(List<Author> authors, Map<Author, List<Project>> map){
        TextClassifier textClassifier = new TextClassifier();
        for(Author author : authors){
            List<Project> projects;
            projects = map.get(author);
            if(projects.isEmpty())
                continue;
            textClassifier.addCategory(author.getExpertidtk().toString());
        }
        textClassifier.setupAfterCategorysAdded();
        for(Author author : authors){
            List<Project> projects;
            projects = map.get(author);
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
            return null;
        }
        return textClassifier;
    }

    private static class FindPossibleTextClassForTesting extends FindPossibleTextClass{
        private Map<Author, List<Project>> map;
        public FindPossibleTextClassForTesting(List<Author> authorList, String project, Map<Author, List<Project>> map) {
            super(authorList,project);
            this.map = map;
        }

        @Override
        protected List<Project> getProjects(Author author) {
            return map.get(author);
        }
    }
}

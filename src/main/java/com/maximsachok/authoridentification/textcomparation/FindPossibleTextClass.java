package com.maximsachok.authoridentification.textcomparation;

import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.textvectorization.TextClassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class FindPossibleTextClass implements Callable<Author> {

    protected List<Author> authorList;
    protected String project;


    public FindPossibleTextClass(List<Author> authorList, String project) {
        this.authorList = authorList;
        this.project = project;
    }

    protected List<Project> getProjects(Author author){
        List<Project> projects = new ArrayList<>();
        for(AuthorProject authorProject : author.getAuthorProjects()){
            projects.add(authorProject.getProject());
        }
        return projects;
    }

    protected TextClassifier buildClassifier(List<Author> authors){
        TextClassifier textClassifier = new TextClassifier();
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
            return null;
        }
        return textClassifier;
    }

    @Override
    public Author call() {
        if(authorList.size()==1)
            return authorList.get(0);
        boolean doNotCompare;
        TextClassifier textClassifier = buildClassifier(authorList);
        if(textClassifier==null){
            return null;
        }
        double[] classifierResult;
        try{
            classifierResult = textClassifier.classifyMessage(project);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        double maxScore = 0;
        Author possibleAuthor = null;
        for(Author author : authorList)
        {
            doNotCompare = false;
            for(Project project1 : getProjects(author)){
                if(project1.asString().equals(project)){
                    doNotCompare = true;
                    break;
                }
            }
            if(doNotCompare)
                continue;
            if(textClassifier.getClassIndex(author.getExpertidtk().toString())>-1 &&
                    textClassifier.getClassIndex(author.getExpertidtk().toString())<classifierResult.length){
                if(maxScore<(classifierResult[textClassifier.getClassIndex(author.getExpertidtk().toString())])){
                    maxScore = (classifierResult[textClassifier.getClassIndex(author.getExpertidtk().toString())]);
                    possibleAuthor = author;
                }
            }
        }
        return possibleAuthor;
    }
}

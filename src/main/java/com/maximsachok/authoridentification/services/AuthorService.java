package com.maximsachok.authoridentification.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maximsachok.authoridentification.AuthorIdentificationApplication;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.Response;
import com.maximsachok.authoridentification.dto.Result;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.textvectorization.CosineSimilarity;
import com.maximsachok.authoridentification.textvectorization.TextClassifier;
import com.maximsachok.authoridentification.textvectorization.TextVectorization;
import com.maximsachok.authoridentification.utils.ResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.bayes.NaiveBayesMultinomialText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorIdentificationApplication.class);
    private AuthorRepository authorRepository;
    private ObjectMapper objectMapper;
    private static Boolean updating = false;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, ObjectMapper objectMapper) {
        this.authorRepository = authorRepository;
        this.objectMapper = objectMapper;
    }

    /**Updates vectors of given author.
     * @param author Author for which to calculate new vectors
     * @param projects Authors list of projects
     * @return Null if project list is empty. Author if not.
     */
    private Author update(Author author, List<Project> projects) {
        if(projects.isEmpty())
            return null;
        TextVectorization textVectorization = new TextVectorization();
        long startTime = System.currentTimeMillis();
        Map<String, Double> textTfIdf = textVectorization.calculateTfIdfForAuthor(projects);
        logger.info(">> Time took to compute Tf  => {} ms", (System.currentTimeMillis() - startTime));
        try{
            startTime = System.currentTimeMillis();
            author.setExpertTf(objectMapper.writeValueAsString(textTfIdf));
            logger.info(">> Time took to transform vectors to json  => {} ms", (System.currentTimeMillis() - startTime));
            return author;
        }
        catch (JsonProcessingException ignored){
            return null;
        }
    }

    public double alg(){
        double numberOfAuthors=0;
        double numberOfFound=0;

        List<Author> authorList = authorRepository.findAll();

        Map<Author,List<Project>> map = new HashMap<>();

        Map<Author,Project> deleted = new HashMap<>();

        for(Author author : authorList){
            List<Project> projects = getProjects(author);
            if(projects.size()>=2){
                deleted.put(author,projects.get(0));
                projects.remove(0);
                map.put(author,projects);
                numberOfAuthors++;
            }
        }

        TextClassifier textClassifier = TextClassifier.updateClassifier(new NaiveBayesMultinomialText());
        for(Author author : authorList){
            List<Project> projects;
            projects = getProjects(author);
            if(projects.isEmpty())
                continue;
            textClassifier.addCategory(author.getExpertidtk().toString());
        }
        textClassifier.setupAfterCategorysAdded();
        for(Author author : authorList){
            List<Project> projects;
            if(!map.containsKey(author))
                continue;
            for(Project project: map.get(author)){
                textClassifier.addData(project.asString(),author.getExpertidtk().toString());
            }
        }
        try{
            textClassifier.buildIfNeeded();
        }
        catch (Exception ignored){}

        for(Author author : deleted.keySet()){
            ResultList bayesScore = new ResultList();
            for(Author author2 : authorList){
                if(deleted.containsKey(author)){
                    Result result = new Result();
                    double [] classifierResult = null;
                    try{
                        classifierResult = textClassifier.classifyMessage(deleted.get(author).asString());
                    }
                    catch (Exception ignored){}
                    if(classifierResult!=null
                            && textClassifier.getClassIndex(author.getExpertidtk().toString())>-1
                            && textClassifier.getClassIndex(author.getExpertidtk().toString())<classifierResult.length){
                        result.setBayesScore(classifierResult[textClassifier.getClassIndex(author.getExpertidtk().toString())]);
                    }
                    bayesScore.addItem(result);
                }

            }
            if(bayesScore.getResultList().size()>0 && bayesScore.getResultList().get(0).getAuthorID()==author.getExpertidtk()){
                numberOfFound++;
            }
        }


        return numberOfFound/numberOfAuthors;
    }

    /**
     * Updates vectors for a given authors id
     * @param id Authors id for which to update vectors
     * @return returns true if author exists, false if not.
     */
    synchronized public Boolean updateAuthor(long id){
        updating = true;
        if(authorRepository.findById(id).isEmpty())
            return false;
        Author author = authorRepository.findById(id).get();
        List<Project> projects;
        projects = getProjects(author);
        if(projects.isEmpty())
            return true;
        TextClassifier classifier =  TextClassifier.getTextClassifier(new NaiveBayesMultinomialText());
        classifier.addCategoryAfterSetup(author.getExpertidtk().toString());
        for(Project project : projects){
            classifier.addData(project.asString(), author.getExpertidtk().toString());
        }
        try{
            classifier.buildIfNeeded();
        }
        catch (Exception ignored){}
        if(update(author,projects)!=null)
            authorRepository.save(author);
        return true;
    }

    private List<Project> getProjects(Author author){
        List<Project> projects = new ArrayList<>();
        for(AuthorProject authorProject : author.getAuthorProjects()){
            projects.add(authorProject.getProject());
        }
        return projects;
    }

    private void updateClassifier(List<Author> authors){
        TextClassifier textClassifier = TextClassifier.updateClassifier(new NaiveBayesMultinomialText());
        for(Author author : authors){
            List<Project> projects = new ArrayList<>();
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
        catch (Exception ignored){}

    }

    /**
     * Updates vectors for all authors.
     */
    synchronized public void updateAllAuthors()  {
        updating = true;
        long startTime;
        List<Author> authorList = new ArrayList<>();
        List<Author> authors = authorRepository.findAll();
        updateClassifier(authors);
        for(Author author : authors){
            List<Project> projects;
            projects = getProjects(author);
            if(projects.isEmpty())
                continue;
            if(update(author, projects) != null)
                authorList.add(author);
        }
        logger.info("Start saving");
        startTime = System.currentTimeMillis();
        authorRepository.saveAll(authorList);
        logger.info(">> Time took to save authors  => {} ms, number of authors saved =>{}", (System.currentTimeMillis() - startTime), authorList.size());
    }

    /**
     * Looks through all authors and finds possible authors that could have written given project.
     * @param project Project for which to find possible author
     * @return Response object that contains 3 lists sorted in descending order based on wordscore, tfscore and  both(with priority given to wordscore).
     * @see Response
     */
    public Response findPossibleAuthor(ProjectDto project) {
        ResultList tfTop = new ResultList();
        ResultList bayesScore = new ResultList();
        Response response = new Response();

        tfTop.setComparator((a, b) -> {
            if(a.getTfScore()<b.getTfScore())
                return 1;
            else if(a.getTfScore()>b.getTfScore())
                return -1;
            return 0;
        });
        bayesScore.setComparator((a, b) ->{
            if(a.getBayesScore()<b.getBayesScore())
                    return 1;
            else if(a.getBayesScore()>b.getBayesScore())
                    return -1;
            return 0;
        });
        CosineSimilarity similarity = new CosineSimilarity();

        TextVectorization textVectorization = new TextVectorization();
        double[] classifierResult = null;
        TextClassifier textClassifier = null;
        if(TextClassifier.isInitialized()){
            textClassifier = TextClassifier.getTextClassifier(new NaiveBayesMultinomialText());
            try{
                classifierResult = textClassifier.classifyMessage(project.asString());
            }
            catch (Exception ignored){}
        }
        Map<String, Double> projectTF = textVectorization.mapProject(project);
        for(Author author : authorRepository.findAll())
        {
            Map<String, Double> authorIDF;
            List<Project> projects = new ArrayList<>();
            projects = getProjects(author);

            if(projects.isEmpty())
                continue;

            if(author.getExpertTf() == null || author.getExpertWordVec()==null)
                continue;
            try{
                authorIDF = objectMapper.readValue(author.getExpertTf(), new TypeReference<Map<String, Double>>(){});
                logger.info("Map formatted ok");
            }
            catch (JsonProcessingException ignored){
                logger.info("Error while doing json to java formatting.");
                continue;
            }


            if(!authorIDF.isEmpty()){
                Map<String, Double> projectIDF = textVectorization.calculateTfIdfForProject(projectTF,projects);
                Result result = new Result();
                result.setAuthorID(author.getExpertidtk());
                result.setTfScore(similarity.compareTwoMaps(projectIDF,authorIDF));
                if(classifierResult!=null && textClassifier.getClassIndex(author.getExpertidtk().toString())>-1 &&
                   textClassifier.getClassIndex(author.getExpertidtk().toString())<classifierResult.length){
                    result.setBayesScore(classifierResult[textClassifier.getClassIndex(author.getExpertidtk().toString())]);
                }
                tfTop.addItem(result);
                bayesScore.addItem(result);
            }
        }
        response.setBayesTop(bayesScore.getResultList());
        response.setTfTop(tfTop.getResultList());
        return response;
    }

    public Boolean isUpdating(){
        return updating;
    }

    public void setUpdating(Boolean updating){
        AuthorService.updating=updating;
    }

}

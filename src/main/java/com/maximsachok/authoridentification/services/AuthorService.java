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
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.textvectorization.CosineSimilarity;
import com.maximsachok.authoridentification.textvectorization.TextVectorization;
import com.maximsachok.authoridentification.utils.ResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.*;

@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorIdentificationApplication.class);
    private AuthorRepository authorRepository;
    private ProjectRepository projectRepository;
    private AuthorProjectRepository authorProjectRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, ProjectRepository projectRepository, AuthorProjectRepository authorProjectRepository,ObjectMapper objectMapper) {
        this.authorRepository = authorRepository;
        this.projectRepository = projectRepository;
        this.authorProjectRepository = authorProjectRepository;
        this.objectMapper = objectMapper;
    }

    public Author update(Author author, List<Project> projects) {
        if(projects.isEmpty())
            return null;
        TextVectorization textVectorization = new TextVectorization();
        long startTime = System.currentTimeMillis();
        double[] textWordVec = textVectorization.vectoriseProjects(projects);
        Map<String, Double> textTf = textVectorization.mapProjects(projects);
        logger.info(">> Time took to compute Word2Vec and Tf  => {} ms", (System.currentTimeMillis() - startTime));
        try{
            startTime = System.currentTimeMillis();
            author.setExpertWordVec(objectMapper.writeValueAsString(textWordVec));
            author.setExpertTf(objectMapper.writeValueAsString(textTf));
            logger.info(">> Time took to transform vectors to json  => {} ms", (System.currentTimeMillis() - startTime));
            return author;
        }
        catch (JsonProcessingException ignored){
            return null;
        }
    }

    public void saveAuthor(Author author){
        authorRepository.save(author);
    }
    public void  saveAllAuthors(List<Author> authors){
        logger.info(">> Saving authors");
        long startTime = System.currentTimeMillis();
        long startTime2 = System.currentTimeMillis();
        List<Author> authorList = new ArrayList<>();
        for(Author author : authors){
            authorList.add(author);
            if(authorList.size() % 3 == 0){
                logger.info(">> Saving 3 authors");
                authorRepository.saveAll(authorList);
                logger.info("Time took to save batch of 3 authors {} ms", System.currentTimeMillis() - startTime2);
                authorList.clear();
                startTime2 = System.currentTimeMillis();
            }
        }
        if(!authorList.isEmpty())
            authorRepository.saveAll(authors);
        logger.info(">> Time took to update all authors  => {} ms  {} authors", (System.currentTimeMillis() - startTime), authors.size());
    }

    @Transactional
    public String updateAllAuthors() {
        long startTime;
        List<Author> authorList = new ArrayList<>();
        for(Author it : authorRepository.findAll()){
            startTime = System.currentTimeMillis();
            List<Project> projects = new ArrayList<>();
            for(AuthorProject authorProject : it.getAuthorProjects()){
                projects.add(authorProject.getProject());
            }
            if(projects.isEmpty())
                continue;

            if(update(it, projects) != null)
                authorList.add(it);
            /*it.setExpertTf(" ");
            it.setExpertWordVec(" ");
            authorList.add(it);*/
            /*if(author != null)
                authorRepository.save(author);*/

        }
        logger.info("Start saving");
        startTime = System.currentTimeMillis();
        authorRepository.saveAll(authorList);
        logger.info(">> Time took to save authors  => {} ms, size =>{}", (System.currentTimeMillis() - startTime), authorList.size());
        return (">> Time took to save authors  =>"+(System.currentTimeMillis() - startTime)+"ms, size =>"+ authorList.size());
    }

    public String testing(){
        long startTime = System.currentTimeMillis();
        for(long i=0;i<10000;i++){
            startTime = System.currentTimeMillis();
            Optional<Author> author = authorRepository.findById(i);
            if(!author.isPresent())
                continue;
            author.get().setExpertWordVec("");
            author.get().setExpertTf("");
            authorRepository.save(author.get());
            logger.info(">> Time took to save author  => {} ms", (System.currentTimeMillis() - startTime));
        }
        return "ok";
    }

    public String testingTime(){
        long startTime = System.currentTimeMillis();
            startTime = System.currentTimeMillis();
            Optional<Author> author = authorRepository.findById(10L);
            if(!author.isPresent())
                return "bad";
            author.get().setExpertWordVec("");
            author.get().setExpertTf("");
            authorRepository.save(author.get());
            logger.info(">> Time took to save author  => {} ms", (System.currentTimeMillis() - startTime));
        return "ok";
    }

    public Iterable<Author> findAllAuthors(){
        return authorRepository.findAll();
    }

    public Response findPossibleAuthor(ProjectDto project) {
        ResultList wordVecTop = new ResultList();
        ResultList tfTop = new ResultList();
        ResultList bothTop = new ResultList();
        Response response = new Response();

        wordVecTop.setComparator((a, b) -> {
            if(a.getWordScore()<b.getWordScore())
                return 1;
            else if(a.getWordScore()>b.getWordScore())
                return -1;
            return 0;
        });
        tfTop.setComparator((a, b) -> {
            if(a.getTfScore()<b.getTfScore())
                return 1;
            else if(a.getTfScore()>b.getTfScore())
                return -1;
            return 0;
        });
        bothTop.setComparator((a, b) ->{
            if(a.getWordScore()<b.getWordScore())
                    return 1;
            else if(a.getWordScore()>b.getWordScore())
                    return -1;
            else if(a.getTfScore()<b.getTfScore())
                return 1;
            else if(a.getTfScore()>b.getTfScore())
                return -1;

            return 0;
        });
        CosineSimilarity similarity = new CosineSimilarity();

        TextVectorization textVectorization = new TextVectorization();
        double[] projectWord2Vec = textVectorization.vectoriseProject(project);
        Map<String, Double> projectTF = textVectorization.mapProject(project);
        for(Author author : findAllAuthors())
        {
            Map<String, Double> authorTF;
            double[] authorWord2Vec;
            List<Project> projects = new ArrayList<>();
            for(AuthorProject authorProject : author.getAuthorProjects()){
                projects.add(authorProject.getProject());
            }

            if(projects.isEmpty())
                continue;

            authorTF = textVectorization.mapProjects(projects);
            authorWord2Vec = textVectorization.vectoriseProjects(projects);

            if(authorWord2Vec.length==300 && !authorTF.isEmpty()){
                Map<String, Double> projectIDF = textVectorization.calculateTfIdf((double)projects.size(),projectTF,authorTF);
                Result result = new Result();
                result.setAuthorID(author.getExpertidtk());
                result.setTfScore(similarity.compareTwoMaps(projectIDF,authorTF));
                result.setWordScore(similarity.compareTwoVectors(projectWord2Vec,authorWord2Vec));
                wordVecTop.addItem(result);
                tfTop.addItem(result);
                bothTop.addItem(result);
            }
        }
        response.setBothTop(bothTop.getResultList());
        response.setTfTop(tfTop.getResultList());
        response.setWordTop(wordVecTop.getResultList());
        return response;
    }

}

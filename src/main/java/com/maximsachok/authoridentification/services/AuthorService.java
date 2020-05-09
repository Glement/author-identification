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
import com.maximsachok.authoridentification.textvectorization.TextVectorization;
import com.maximsachok.authoridentification.utils.ResultList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class AuthorService {

    private static final Logger logger = LoggerFactory.getLogger(AuthorIdentificationApplication.class);
    private AuthorRepository authorRepository;
    private ObjectMapper objectMapper;

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
        double[] textWordVec = textVectorization.vectoriseProjects(projects);
        Map<String, Double> textTfIdf = textVectorization.calculateTfIdfForAuthor(projects);
        logger.info(">> Time took to compute Word2Vec and Tf  => {} ms", (System.currentTimeMillis() - startTime));
        try{
            startTime = System.currentTimeMillis();
            author.setExpertWordVec(objectMapper.writeValueAsString(textWordVec));
            author.setExpertTf(objectMapper.writeValueAsString(textTfIdf));
            logger.info(">> Time took to transform vectors to json  => {} ms", (System.currentTimeMillis() - startTime));
            return author;
        }
        catch (JsonProcessingException ignored){
            return null;
        }
    }

    /**
     * Updates vectors for a given authors id
     * @param id Authors id for which to update vectors
     * @return returns true if author exists, false if not.
     */
    public Boolean updateAuthor(long id){
        if(authorRepository.findById(id).isEmpty())
            return false;
        Author author = authorRepository.findById(id).get();
        List<Project> projects = new ArrayList<>();
        projects = getProjects(author);
        if(projects.isEmpty())
            return true;
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

    /**
     * Updates vectors for all authors.
     */
    public void updateAllAuthors() {
        long startTime;
        List<Author> authorList = new ArrayList<>();
        for(Author author : authorRepository.findAll()){
            List<Project> projects = new ArrayList<>();
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
        for(Author author : authorRepository.findAll())
        {
            Map<String, Double> authorIDF;
            double[] authorWord2Vec;
            List<Project> projects = new ArrayList<>();
            projects = getProjects(author);

            if(projects.isEmpty())
                continue;
            if(author.getExpertTf() == null || author.getExpertWordVec()==null)
                continue;
            try{
                authorIDF = objectMapper.readValue(author.getExpertTf(), new TypeReference<Map<String, Double>>(){});
                logger.info("Map formatted ok");
                authorWord2Vec = objectMapper.readValue(author.getExpertWordVec(),double[].class);
                logger.info("Double formatted ok");
            }
            catch (JsonProcessingException ignored){
                logger.info("Error while doing json to java formatting.");
                continue;
            }

            if(authorWord2Vec.length==300 && !authorIDF.isEmpty()){
                Map<String, Double> projectIDF = textVectorization.calculateTfIdfForProject(projectTF,projects);
                Result result = new Result();
                result.setAuthorID(author.getExpertidtk());
                result.setTfScore(similarity.compareTwoMaps(projectIDF,authorIDF));
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

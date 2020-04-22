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
        ProjectDto projectDto = new ProjectDto();
        StringBuilder allProject = new StringBuilder();
        long startTime = System.currentTimeMillis();
        for(Project project : projects)
        {
            allProject.append(" ").append(project.getDescEn()).append(" ").append(project.getNameEn()).append(" ").append(project.getKeywords());
        }
        projectDto.setDescEn(allProject.toString());
        double[] textWordVec = textVectorization.vectoriseProject(projectDto);
        Map<String, Double> textTfIdf = textVectorization.calculateTfIdf(projects,textVectorization.mapProjects(projects));
        logger.info(">> Time took to compute Word2Vec and TfIdf  => {} ms", (System.currentTimeMillis() - startTime));
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
            if(authorList.size() % 5 == 0){
                logger.info(">> Saving 5 authors");
                authorRepository.saveAll(authorList);
                logger.info("Time took to update batch of 5 {} ms", System.currentTimeMillis() - startTime2);
                authorList.clear();
                startTime2 = System.currentTimeMillis();
            }
        }
        if(!authorList.isEmpty())
            authorRepository.saveAll(authors);
        logger.info(">> Time took to update all authors  => {} ms  {} authors", (System.currentTimeMillis() - startTime), authors.size());
    }

    public void updateAllAuthors() {
        Map<BigInteger, Set<BigInteger>> authorProjectsMap = new HashMap<>();
        Map<BigInteger,Project> projectsMap = new HashMap<>();
        loadProjects(authorProjectsMap, projectsMap);
        for(Author it : authorRepository.findAll()){
            List<Project> projects = new ArrayList<>();
            if(authorProjectsMap.containsKey(it.getExpertidtk())){
                for(BigInteger integer : authorProjectsMap.get(it.getExpertidtk())){
                    if(projectsMap.containsKey(integer))
                        projects.add(projectsMap.get(integer));
                }
            }
            if(projects.isEmpty())
                continue;

            Author author = update(it, projects);
            long startTime = System.currentTimeMillis();
            if(author != null)
                authorRepository.save(author);
            logger.info("Time to save author => {} ms", System.currentTimeMillis() - startTime);
        }
    }

    public Optional<Author> findAuthorById(BigInteger id){
        return authorRepository.findById(id);
    }
    public Iterable<Author> findAllAuthors(){
        return authorRepository.findAll();
    }


    private void loadProjects(Map<BigInteger, Set<BigInteger>> authorProjectsMap, Map<BigInteger,Project> projectsMap){
        Iterable<Author> authorIterable = authorRepository.findAll();
        Iterable<Project> projectIterable = projectRepository.findAll();
        Iterable<AuthorProject> authorProjectsIterable = authorProjectRepository.findAll();
        for(AuthorProject authorProject : authorProjectsIterable){
            if(authorProjectsMap.containsKey(authorProject.getExpertId()))
                authorProjectsMap.get(authorProject.getExpertId()).add(authorProject.getProjectId());
            else{
                Set<BigInteger> set = new HashSet<>();
                set.add(authorProject.getProjectId());
                authorProjectsMap.put(authorProject.getExpertId(),set);
            }
            logger.info(authorProject.getProjectId().toString() + authorProject.getRole());
        }
        for(Project project : projectIterable){
            projectsMap.put(project.getProjectIdTk(), project);
        }
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
        double[] text1Word2Vec = textVectorization.vectoriseProject(project);
        Map<String, Double> text1TF = textVectorization.mapProject(project);
        Map<BigInteger, Set<BigInteger>> authorProjectsMap = new HashMap<>();
        Map<BigInteger,Project> projectsMap = new HashMap<>();
        loadProjects(authorProjectsMap, projectsMap);
        for(Author author : findAllAuthors())
        {
            Map<String, Double> text2TF;
            double[] text2Word2Vec;
            List<Project> projects = new ArrayList<>();
            if(authorProjectsMap.containsKey(author.getExpertidtk())){
                for(BigInteger integer : authorProjectsMap.get(author.getExpertidtk())){
                    if(projectsMap.containsKey(integer))
                        projects.add(projectsMap.get(integer));
                }
            }

            if(projects.isEmpty())
                continue;

            text2TF = textVectorization.mapProjects(projects);
            text2Word2Vec = textVectorization.vectoriseProjects(projects);

            if(text2Word2Vec.length==300 && !text2TF.isEmpty()){
                Map<String, Double> projectTfIdf = textVectorization.calculateTfIdf(projects,text1TF);
                Result result = new Result();
                result.setAuthorID(author.getExpertidtk());
                result.setTfScore(similarity.compareTwoMaps(projectTfIdf,text2TF));
                result.setWordScore(similarity.compareTwoVectors(text1Word2Vec,text2Word2Vec));
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

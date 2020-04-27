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
        //logger.info(">> Time took to compute Word2Vec and Tf  => {} ms", (System.currentTimeMillis() - startTime));
        try{
            startTime = System.currentTimeMillis();
            author.setExpertWordVec(objectMapper.writeValueAsString(textWordVec));
            author.setExpertTf(objectMapper.writeValueAsString(textTf));
            //logger.info(">> Time took to transform vectors to json  => {} ms", (System.currentTimeMillis() - startTime));
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

    public void testall(){
        Map<BigInteger, Set<BigInteger>> authorProjectsMap = new HashMap<>();
        Map<BigInteger,Project> projectsMap = new HashMap<>();
        loadProjects(authorProjectsMap, projectsMap);
        long startTime;
        List<Author> authorList = new ArrayList<>();
        for(Author it : authorRepository.findAll()){
            Map<String, BigInteger> text1 = new HashMap<>();
            double[] wordvec1 = new double[300];
            Map<String, BigInteger> text2 = new HashMap<>();
            double[] wordvec2 = new double[300];
            startTime = System.currentTimeMillis();
            List<Project> projects = new ArrayList<>();
            if(authorProjectsMap.containsKey(it.getExpertidtk())){
                for(BigInteger integer : authorProjectsMap.get(it.getExpertidtk())){
                    if(projectsMap.containsKey(integer))
                        projects.add(projectsMap.get(integer));
                }
            }
            if(projects.isEmpty())
                continue;
            try{
                wordvec1 = objectMapper.readValue(it.getExpertWordVec(), double[].class);
                text1 = objectMapper.readValue(it.getExpertTf(), new TypeReference<Map<String,BigInteger>>(){});
            }
            catch (JsonProcessingException ex){
                logger.info("Error on author with id {}", it.getExpertidtk());
            }
            Author author = update(it, projects);
            try{
                wordvec2 = objectMapper.readValue(author.getExpertWordVec(), double[].class);
                text2 = objectMapper.readValue(author.getExpertTf(), new TypeReference<Map<String,BigInteger>>(){});
            }
            catch (JsonProcessingException ex){
                logger.info("Error on author with id {}", it.getExpertidtk());
            }
            if(wordvec1.length!=300 || wordvec2.length!=300){
                logger.info("Error on author with id {}", it.getExpertidtk());
            }
            for(int i=0;i<wordvec1.length;++i){
                if(wordvec1[i]!=wordvec2[i]){
                    logger.info("Author {} have wrong word2vec in db, have {}\n must have {}", author.getExpertidtk(), wordvec1, wordvec2);
                    break;
                }
            }
            if(text1.size()!=text2.size() || text1.size()<1 || text2.size()<1){
                logger.info("Error on author with id {}", it.getExpertidtk());
            }
            for(String k : text1.keySet()){
                if(!text2.containsKey(k)){
                    logger.info("Author {} have wrong tf in db, have {}\n must have {}", author.getExpertidtk(), text1, text2);
                    break;
                }
            }


        }
    }

    public String updateAllAuthors() {
        Map<BigInteger, Set<BigInteger>> authorProjectsMap = new HashMap<>();
        Map<BigInteger,Project> projectsMap = new HashMap<>();
        loadProjects(authorProjectsMap, projectsMap);
        long startTime;
        List<Author> authorList = new ArrayList<>();
        for(Author it : authorRepository.findAll()){
            startTime = System.currentTimeMillis();
            List<Project> projects = new ArrayList<>();
            if(authorProjectsMap.containsKey(it.getExpertidtk())){
                for(BigInteger integer : authorProjectsMap.get(it.getExpertidtk())){
                    if(projectsMap.containsKey(integer))
                        projects.add(projectsMap.get(integer));
                }
            }
            if(projects.isEmpty())
                continue;

            if(update(it, projects) != null)
                authorList.add(it);

            /*if(author != null)
                authorRepository.save(author);*/

        }
        logger.info("Start saving");
        startTime = System.currentTimeMillis();
        authorRepository.saveAll(authorList);
        logger.info(">> Time took to save authors  => {} ms, size =>{}", (System.currentTimeMillis() - startTime), authorList.size());
        return (">> Time took to save authors  =>"+(System.currentTimeMillis() - startTime)+"ms, size =>"+ authorList.size());
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
                Map<String, Double> projectTfIdf = textVectorization.calculateTfIdf((double)projects.size(),text1TF,text2TF);
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

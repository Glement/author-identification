package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.textcomparation.FindPossibleTextClass;
import com.maximsachok.authoridentification.textvectorization.TestClassifier;
import com.maximsachok.authoridentification.utils.DivideList;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
public class AuthorService {

    private AuthorRepository authorRepository;
    private ProjectRepository projectRepository;
    private AuthorProjectRepository authorProjectRepository;
    @Autowired
    public AuthorService(AuthorRepository authorRepository, ProjectRepository projectRepository, AuthorProjectRepository authorProjectRepository) {
        this.authorRepository = authorRepository;
        this.projectRepository = projectRepository;
        this.authorProjectRepository = authorProjectRepository;
    }

    public List<Author> getAuthors(){
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthor(Long id){
        return authorRepository.findById(id);
    }

    public boolean deleteAuthor(Long id){
        if(authorRepository.findById(id).isPresent()){
            authorRepository.delete(authorRepository.findById(id).get());
            return true;
        }
        return false;
    }


    public List<ProjectDto> removeProject(Author author, Project project){
        List<ProjectDto> projects = new ArrayList<>();
        for(AuthorProject authorProject : author.getAuthorProjects()){
            if(authorProject.getProject().getProjectIdTk().equals(project.getProjectIdTk())){
                authorProjectRepository.delete(authorProject);
            }
            else
                projects.add(projectToProjectDto(authorProject.getProject()));
        }
        return projects;
    }

    public Long createAuthor(AuthorDto author){
        Author author1 = new Author();
        return authorRepository.save(author1).getExpertidtk();
    }


    public List<ProjectDto> addProject(Author author, Project project){
        AuthorProject authorProject = new AuthorProject();
        authorProject.setProject(project);
        authorProject.setAuthor(author);
        authorProject = authorProjectRepository.save(authorProject);
        author.getAuthorProjects().add(authorProject);
        authorRepository.save(author);
        project.getAuthorProjects().add(authorProject);
        projectRepository.save(project);
        return getAuthorProjectsDto(author.getExpertidtk());
    }

    private ProjectDto projectToProjectDto(Project project){
        ProjectDto projectDto = new ProjectDto();
        projectDto.setDescEn(project.getDescEn());
        projectDto.setKeywords(project.getKeywords());
        projectDto.setNameEn(project.getNameEn());
        projectDto.setId(project.getProjectIdTk());
        return  projectDto;
    }

    public List<ProjectDto> getAuthorProjectsDto(Long id){
        if(authorRepository.findById(id).isEmpty()){
            return null;
        }
        List<ProjectDto> projects = new ArrayList<>();
        for(AuthorProject authorProject : authorRepository.findById(id).get().getAuthorProjects()){
            projects.add(projectToProjectDto(authorProject.getProject()));
        }
        return projects;
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

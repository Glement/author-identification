package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.SearchResultDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.AuthorProjectCompositeId;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.textvectorization.AuthorClassifier;
import com.maximsachok.authoridentification.textvectorization.WekaClassifier;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;


@Service
public class AuthorService {

    private AuthorRepository authorRepository;
    private ProjectRepository projectRepository;
    private AuthorProjectRepository authorProjectRepository;
    private static AuthorClassifier authorClassifier;
    private static AtomicBoolean initialized = new AtomicBoolean(false);
    private static AtomicBoolean initializing = new AtomicBoolean(false);
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
        AuthorProjectCompositeId authorProjectCompositeId = new AuthorProjectCompositeId();
        authorProjectCompositeId.setAuthor(author.getExpertidtk());
        authorProjectCompositeId.setProject(project.getProjectIdTk());
        if(authorProjectRepository.findById(authorProjectCompositeId).isEmpty())
        {
            authorProject = authorProjectRepository.save(authorProject);
            author.getAuthorProjects().add(authorProject);
            authorRepository.save(author);
            project.getAuthorProjects().add(authorProject);
            projectRepository.save(project);
        }
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

    public double testAlgorithm(){
        return 0;
    }

    /**
     * Looks through all authors and finds possible author that could have written given project, if author already have this project it is excluded from search.
     * @param project Project for which to find possible author
     * @return List of  ids of possible authors of given text, default number is 10
     */
    public List<SearchResultDto> findPossibleAuthor(ProjectDto project){
        if(initializing.get())
            return new ArrayList<>();
        if(!initialized.get()){
            initializing.set(true);
            List<Author> allAuthors = authorRepository.findAll();
            authorClassifier = new WekaClassifier();
            authorClassifier.initClassifier(allAuthors);
            initialized.set(true);
            initializing.set(false);
        }
        List<SearchResultDto> result = new ArrayList<>();
        for(ImmutablePair<Double, String> pair : authorClassifier.classifyText(project.asString())){
            AuthorDto authorDto = new AuthorDto();
            authorDto.setId(Long.decode(pair.getValue()));
            result.add(new SearchResultDto(authorDto, pair.getKey()));
        }
        return result;
    }






}

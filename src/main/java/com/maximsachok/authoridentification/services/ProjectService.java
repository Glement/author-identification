package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.AuthorProjectCompositeId;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private ProjectRepository projectRepository;
    private AuthorProjectRepository authorProjectRepository;
    private AuthorRepository authorRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, AuthorProjectRepository authorProjectRepository, AuthorRepository authorRepository) {
        this.authorProjectRepository = authorProjectRepository;
        this.projectRepository = projectRepository;
        this.authorRepository = authorRepository;
    }

    public static ProjectDto projectToProjectDto(Project project){
        ProjectDto projectDto = new ProjectDto();
        projectDto.setDescEn(project.getDescEn());
        projectDto.setKeywords(project.getKeywords());
        projectDto.setNameEn(project.getNameEn());
        projectDto.setId(project.getProjectIdTk());
        return  projectDto;
    }

    /**
     * Removes author from project
     * @param author author to remove
     * @param project project from which to remove an author
     * @return list of authors for this project
     */
    public List<AuthorDto> removeAuthor(Author author, Project project){
        List<AuthorDto> authors = new ArrayList<>();
        for(AuthorProject authorProject : project.getAuthorProjects()){
            if(authorProject.getAuthor().getExpertidtk().equals(author.getExpertidtk())){
                authorProjectRepository.delete(authorProject);
            }
            else
                authors.add(AuthorService.AuthorToAuthorDto(authorProject.getAuthor()));
        }
        return authors;
    }

    /**
     * Adds author to project authors
     *
     * @param author author which to add
     * @param project project to which to add an author
     * @return list of Authors
     */
    public Optional<List<AuthorDto>> addAuthor(Author author, Project project) {
        AuthorProject authorProject = new AuthorProject();
        authorProject.setProject(project);
        authorProject.setAuthor(author);
        AuthorProjectCompositeId authorProjectCompositeId = new AuthorProjectCompositeId();
        authorProjectCompositeId.setAuthor(author.getExpertidtk());
        authorProjectCompositeId.setProject(project.getProjectIdTk());
        if (authorProjectRepository.findById(authorProjectCompositeId).isEmpty()) {
            authorProject = authorProjectRepository.save(authorProject);
            author.getAuthorProjects().add(authorProject);
            authorRepository.save(author);
            project.getAuthorProjects().add(authorProject);
            projectRepository.save(project);
        }
        return getProjectAuthors(project.getProjectIdTk());
    }


    public Optional<List<AuthorDto>> getProjectAuthors(Long id){
        if(projectRepository.findById(id).isEmpty()){
            return Optional.empty();
        }
        List<AuthorDto> authors = new ArrayList<>();
        for(AuthorProject authorProject : projectRepository.findById(id).get().getAuthorProjects()){
            authors.add(new AuthorDto(authorProject.getAuthor().getExpertidtk()));
        }
        return Optional.of(authors);
    }

    public List<Project> getProjects(){
        return projectRepository.findAll();
    }

    public Optional<Project> getProject(Long id){
        return projectRepository.findById(id);
    }

    public boolean deleteProject(Long id){
        if(projectRepository.findById(id).isPresent()){
            projectRepository.delete(projectRepository.findById(id).get());
            return true;
        }
        return false;
    }

    public Long createProject(ProjectDto project){
        return projectRepository.saveAndFlush(ProjectService.projectDtoToProject(project)).getProjectIdTk();
    }

    public static Project projectDtoToProject(ProjectDto projectDto){
        Project project = new Project();
        project.setNameEn(projectDto.getNameEn());
        project.setKeywords(projectDto.getKeywords());
        project.setDescEn(projectDto.getDescEn());
        project.setProjectIdTk(projectDto.getId());
        return project;
    }

    public void updateProject(Project project){
        projectRepository.save(project);
    }
}

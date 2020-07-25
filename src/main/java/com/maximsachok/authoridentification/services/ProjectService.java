package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
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

    @Autowired
    public ProjectService(ProjectRepository projectRepository, AuthorProjectRepository authorProjectRepository) {
        this.authorProjectRepository = authorProjectRepository;
        this.projectRepository = projectRepository;
    }

    public static ProjectDto projectToProjectDto(Project project){
        ProjectDto projectDto = new ProjectDto();
        projectDto.setDescEn(project.getDescEn());
        projectDto.setKeywords(project.getKeywords());
        projectDto.setNameEn(project.getNameEn());
        projectDto.setId(project.getProjectIdTk());
        return  projectDto;
    }

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

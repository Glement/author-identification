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

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

   /* public  Project projectDtoToProject(ProjectDto projectDto){
        Project project = new Project();
        project.setDescEn(projectDto.getDescEn());
        project.setKeywords(projectDto.getKeywords());
        project.setNameEn(projectDto.getNameEn());
        return project;
    }*/

    public List<AuthorDto> getProjectAuthors(Long id){
        if(projectRepository.findById(id).isEmpty()){
            return null;
        }
        List<AuthorDto> authors = new ArrayList<>();
        for(AuthorProject authorProject : projectRepository.findById(id).get().getAuthorProjects()){
            authors.add(new AuthorDto(authorProject.getAuthor().getExpertidtk()));
        }
        return authors;
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
        Project project1 = new Project();
        project1.setNameEn(project.getNameEn());
        project1.setKeywords(project.getKeywords());
        project1.setDescEn(project.getDescEn());
        return projectRepository.save(project1).getProjectIdTk();
    }

    public Long updateProject(Project project){
        return projectRepository.save(project).getProjectIdTk();
    }
}

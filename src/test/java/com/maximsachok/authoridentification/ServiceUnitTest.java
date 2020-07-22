package com.maximsachok.authoridentification;

import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.services.AuthorProjectService;
import com.maximsachok.authoridentification.services.AuthorService;
import com.maximsachok.authoridentification.services.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ServiceUnitTest {

    @Test
    public void createAuthorTest(){
        AuthorRepository authorRepository = mock(AuthorRepository.class);
        AuthorService authorService = new AuthorService(authorRepository,null,null);
        Author author = new Author();
        author.setExpertidtk(1L);
        when(authorRepository.save(any())).thenReturn(author);
        assertEquals(authorService.createAuthor(null),author.getExpertidtk());
    }

    @Test
    public void createProjectTest(){
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        AuthorProjectRepository authorProjectRepository = mock(AuthorProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepository, authorProjectRepository);
        Project project = new Project();
        project.setDescEn("a");
        project.setNameEn("b");
        project.setKeywords("c");
        project.setProjectIdTk(1L);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setDescEn("a");
        projectDto.setNameEn("b");
        projectDto.setKeywords("c");
        when(projectRepository.save(any())).thenReturn(project);
        assertEquals(projectService.createProject(projectDto),project.getProjectIdTk());
    }

    @Test
    public void deleteProjectTest(){
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        AuthorProjectRepository authorProjectRepository = mock(AuthorProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepository, authorProjectRepository);
        Project project = new Project();
        project.setDescEn("a");
        project.setNameEn("b");
        project.setKeywords("c");
        project.setProjectIdTk(1L);
        when(projectRepository.findById(1L)).thenReturn(java.util.Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        projectService.deleteProject(1L);
        verify(projectRepository,times(1)).delete(project);
    }

    @Test
    public void deleteAuthorTest(){
        AuthorRepository authorRepository = mock(AuthorRepository.class);
        AuthorService authorService = new AuthorService(authorRepository,null,null);
        Author author = new Author();
        author.setExpertidtk(1L);
        when(authorRepository.findById(1L)).thenReturn(java.util.Optional.of(author));
        authorService.deleteAuthor(1L);
        verify(authorRepository,times(1)).delete(author);
    }

    @Test
    public void updateProjectTest(){
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        AuthorProjectRepository authorProjectRepository = mock(AuthorProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepository, authorProjectRepository);
        Project project = new Project();
        project.setDescEn("a");
        project.setNameEn("b");
        project.setKeywords("c");
        project.setProjectIdTk(1L);
        when(projectRepository.findById(1L)).thenReturn(java.util.Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        projectService.updateProject(project);
        verify(projectRepository,times(1)).save(project);
    }

    @Test
    public void removeProjectFromAuthorTest(){
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        AuthorProjectRepository authorProjectRepository = mock (AuthorProjectRepository.class);
        AuthorProjectService authorProjectService = new AuthorProjectService(authorProjectRepository);
        AuthorProject authorProject = new AuthorProject();

        ProjectService projectService = new ProjectService(projectRepository, authorProjectRepository);
        Project project = new Project();
        project.setDescEn("a");
        project.setNameEn("b");
        project.setKeywords("c");
        project.setProjectIdTk(1L);
        when(projectRepository.findById(1L)).thenReturn(java.util.Optional.of(project));

        authorProject.setProject(project);
        AuthorRepository authorRepository = mock(AuthorRepository.class);
        AuthorService authorService = new AuthorService(authorRepository,projectRepository,authorProjectRepository);
        Author author = new Author();
        author.setExpertidtk(1L);
        authorProject.setAuthor(author);
        Set<AuthorProject> authorSet = new HashSet<>();
        authorSet.add(authorProject);
        author.setAuthorProjects(authorSet);
        when(authorRepository.findById(1L)).thenReturn(java.util.Optional.of(author));
        authorService.removeProject(author,project);
        verify(authorProjectRepository,times(1)).delete(authorProject);
    }

    @Test
    public void addProjectToAuthorTest(){
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        AuthorProjectRepository authorProjectRepository = mock (AuthorProjectRepository.class);
        AuthorProjectService authorProjectService = new AuthorProjectService(authorProjectRepository);
        AuthorProject authorProject = new AuthorProject();
        ProjectService projectService = new ProjectService(projectRepository, authorProjectRepository);
        Project project = new Project();
        project.setDescEn("a");
        project.setNameEn("b");
        project.setKeywords("c");
        project.setProjectIdTk(1L);
        Set<AuthorProject> authorProjectSet = new HashSet<>();
        project.setAuthorProjects(authorProjectSet);
        when(projectRepository.findById(1L)).thenReturn(java.util.Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);

        authorProject.setProject(project);
        when(authorProjectRepository.save(any())).thenReturn(authorProject);


        AuthorRepository authorRepository = mock(AuthorRepository.class);
        AuthorService authorService = new AuthorService(authorRepository,projectRepository,authorProjectRepository);
        Author author = new Author();
        author.setExpertidtk(1L);
        authorProject.setAuthor(author);
        Set<AuthorProject> authorSet = new HashSet<>();
        authorSet.add(authorProject);
        author.setAuthorProjects(authorSet);
        when(authorRepository.findById(1L)).thenReturn(java.util.Optional.of(author));
        when(authorRepository.save(any())).thenReturn(author);
        authorService.addProject(author,project);
        verify(authorProjectRepository,times(1)).save(any());
    }


    @Test
    public void getProjectAuthorsTest(){
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        AuthorProjectRepository authorProjectRepository = mock(AuthorProjectRepository.class);
        ProjectService projectService = new ProjectService(projectRepository, authorProjectRepository);
        Project project = new Project();
        project.setDescEn("a");
        project.setNameEn("b");
        project.setKeywords("c");
        project.setProjectIdTk(1L);
        Author author = new Author();
        author.setExpertidtk(1L);
        AuthorProject authorProject = new AuthorProject();
        authorProject.setAuthor(author);
        Set<AuthorProject> authorProjectSet = new HashSet<>();
        authorProjectSet.add(authorProject);
        project.setAuthorProjects(authorProjectSet);
        AuthorService authorService = new AuthorService(null,projectRepository,null);
        when(projectRepository.findById(1L)).thenReturn(java.util.Optional.of(project));
        projectService.getProjectAuthors(1L);
        verify(projectRepository,times(2)).findById(1L);
    }


    @Test
    public void getAuthorProjectsTest(){
        AuthorRepository authorRepository = mock(AuthorRepository.class);
        Project project = new Project();
        project.setDescEn("a");
        project.setNameEn("b");
        project.setKeywords("c");
        project.setProjectIdTk(1L);
        Author author = new Author();
        author.setExpertidtk(1L);
        AuthorProject authorProject = new AuthorProject();
        authorProject.setProject(project);
        Set<AuthorProject> authorProjectSet = new HashSet<>();
        authorProjectSet.add(authorProject);
        author.setAuthorProjects(authorProjectSet);
        project.setAuthorProjects(authorProjectSet);
        AuthorService authorService = new AuthorService(authorRepository,null,null);
        when(authorRepository.findById(1L)).thenReturn(java.util.Optional.of(author));
        authorService.getAuthorProjectsDto(1L);
        verify(authorRepository,times(2)).findById(1L);
    }
}

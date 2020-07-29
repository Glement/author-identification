package com.maximsachok.authoridentification;

import com.maximsachok.authoridentification.dto.SearchResultDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.services.AuthorService;
import com.maximsachok.authoridentification.services.ProjectService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServiceIntegrationTest {
    @Test
    public void findPossibleAuthor(){
        AuthorRepository authorRepository = mock(AuthorRepository.class);
        ProjectRepository projectRepository = mock(ProjectRepository.class);
        AuthorProjectRepository authorProjectRepository = mock(AuthorProjectRepository.class);
        AuthorService authorService = new AuthorService(authorRepository, projectRepository, authorProjectRepository);

        Author author1 = new Author();
        author1.setExpertidtk(1L);

        Author author2 = new Author();
        author2.setExpertidtk(2L);

        Project project1 = new Project();
        project1.setProjectIdTk(1L);
        project1.setNameEn("A cat is a cat");
        project1.setDescEn("Cat will be the cat");
        project1.setKeywords("Cat");

        Project project2 = new Project();
        project2.setProjectIdTk(2L);
        project2.setNameEn("A dog is a dog");
        project2.setDescEn("dog will be the dog");
        project2.setKeywords("Dog");
        List<Author> authorList = new ArrayList<>();

        AuthorProject authorProject1 = new AuthorProject();
        authorProject1.setProject(project1);
        authorProject1.setAuthor(author1);
        Set<AuthorProject> authorProjectSet1 = new HashSet<>();
        authorProjectSet1.add(authorProject1);
        author1.setAuthorProjects(authorProjectSet1);

        AuthorProject authorProject2 = new AuthorProject();
        authorProject2.setProject(project2);
        authorProject2.setAuthor(author2);
        Set<AuthorProject> authorProjectSet2 = new HashSet<>();
        authorProjectSet2.add(authorProject2);
        author2.setAuthorProjects(authorProjectSet2);

        authorList.add(author1);
        authorList.add(author2);
        when(authorRepository.findAll()).thenReturn(authorList);
        authorService.initClassifier();
        List<SearchResultDto> searchResultDtoList = authorService.findPossibleAuthor(ProjectService.projectToProjectDto(project1));
        assert(authorService.isClassifierInitialized());
        assert (searchResultDtoList.size()==2);
        assert (searchResultDtoList.get(0).getAuthorDto().getId().equals(AuthorService.AuthorToAuthorDto(author1).getId()));
    }
}

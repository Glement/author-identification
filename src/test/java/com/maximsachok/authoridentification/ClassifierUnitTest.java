package com.maximsachok.authoridentification;

import com.maximsachok.authoridentification.dto.SearchResultDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.services.ProjectService;
import com.maximsachok.authoridentification.textvectorization.WekaClassifier;
import com.maximsachok.authoridentification.textvectorization.WekaClassifierWrapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ClassifierUnitTest {
    @Test
    public void classifierInitTest(){
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

        List<Author> authors = new ArrayList<>();
        authors.add(author1);
        authors.add(author2);
        WekaClassifierWrapper wekaClassifierWrapper = new WekaClassifierWrapper();
        WekaClassifier wekaClassifier = mock(WekaClassifier.class);
        wekaClassifierWrapper.initClassifier(authors, wekaClassifier);
        verify(wekaClassifier, times(1)).initClassifier(authors);
        assertEquals(wekaClassifierWrapper.isInitialized(), true);
        assertFalse(wekaClassifierWrapper.isRefreshRequested());
        assertFalse(wekaClassifierWrapper.isInitializing());
    }

    @Test
    public void classifierClassifyTest(){
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

        List<Author> authors = new ArrayList<>();
        authors.add(author1);
        authors.add(author2);
        WekaClassifierWrapper wekaClassifierWrapper = new WekaClassifierWrapper();
        WekaClassifier wekaClassifier = new WekaClassifier();
        wekaClassifierWrapper.initClassifier(authors, wekaClassifier);
        List<SearchResultDto> searchResultDtoList = wekaClassifierWrapper.findPossibleAuthor(ProjectService.projectToProjectDto(project2));
        assert (searchResultDtoList.get(0).getAuthorDto().getId().equals(author2.getExpertidtk()));

    }

    @Test
    public void classifierFindPossibleAuthorTest(){
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

        List<Author> authors = new ArrayList<>();
        authors.add(author1);
        authors.add(author2);
        WekaClassifierWrapper wekaClassifierWrapper = new WekaClassifierWrapper();
        WekaClassifier wekaClassifier = mock(WekaClassifier.class);
        wekaClassifierWrapper.initClassifier(authors, wekaClassifier);
        verify(wekaClassifier, times(1)).initClassifier(authors);
        assertEquals(wekaClassifierWrapper.isInitialized(), true);
        assertFalse(wekaClassifierWrapper.isRefreshRequested());
        assertFalse(wekaClassifierWrapper.isInitializing());
    }

}

package com.maximsachok.authoridentification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.Response;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.services.AuthorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

@SpringBootTest
public class AuthorServiceTest {

    @Test
    void testUpdateAll() {
        Author author1 = new Author();
        author1.setExpertidtk(1L);
        Author author2 = new Author();
        author2.setExpertidtk(2L);
        Author author3 = new Author();
        author3.setExpertidtk(3L);
        Set<AuthorProject> authorProjectSet = new HashSet<>();
        author3.setAuthorProjects(authorProjectSet);
        Project project1 = new Project();
        project1.setProjectIdTk(1L);
        project1.setDescEn("simulation of unit tests");
        project1.setNameEn("Project number one");
        project1.setKeywords("simulation");
        Project project2 = new Project();
        project2.setProjectIdTk(2L);
        project2.setDescEn("Cats stuck in trees");
        project2.setNameEn("Project number two");
        project2.setKeywords("cats trees");

        AuthorProject authorProject1 = new AuthorProject();
        authorProject1.setAuthor(author1);
        authorProject1.setProject(project1);
        author1.setAuthorProjects(Set.of(authorProject1));

        AuthorProject authorProject2 = new AuthorProject();
        authorProject2.setAuthor(author2);
        authorProject2.setProject(project2);
        author2.setAuthorProjects(Set.of(authorProject2));

        AuthorRepository authorRepository = Mockito.mock(AuthorRepository.class);
        List<Author> authorList = new ArrayList<>();
        authorList.add(author1);
        authorList.add(author2);
        authorList.add(author3);
        Mockito.when(authorRepository.findAll()).thenReturn(authorList);
        Mockito.when(authorRepository.saveAll(authorList)).thenReturn(authorList);

        ObjectMapper objectMapper = new ObjectMapper();
        AuthorService authorService = new AuthorService(authorRepository, objectMapper);
        authorService.updateAllAuthors();
        assertThat(author1.getExpertTf() != null && author1.getExpertTf().length() > 1);
        assertThat(author2.getExpertTf()!=null && author2.getExpertTf().length()>1);
        assertThat(author3.getExpertTf()==null);
    }

    @Test
    void testUpdateAuthor() {
        Author author1 = new Author();
        author1.setExpertidtk(1L);
        Author author2 = new Author();
        author2.setExpertidtk(2L);
        Author author3 = new Author();
        author3.setExpertidtk(3L);
        Set<AuthorProject> authorProjectSet = new HashSet<>();
        author3.setAuthorProjects(authorProjectSet);
        Project project1 = new Project();
        project1.setProjectIdTk(1L);
        project1.setDescEn("simulation of unit tests");
        project1.setNameEn("Project number one");
        project1.setKeywords("simulation");
        Project project2 = new Project();
        project2.setProjectIdTk(2L);
        project2.setDescEn("Cats stuck in trees");
        project2.setNameEn("Project number two");
        project2.setKeywords("cats trees");

        AuthorProject authorProject1 = new AuthorProject();
        authorProject1.setAuthor(author1);
        authorProject1.setProject(project1);
        author1.setAuthorProjects(Set.of(authorProject1));

        AuthorProject authorProject2 = new AuthorProject();
        authorProject2.setAuthor(author2);
        authorProject2.setProject(project2);
        author2.setAuthorProjects(Set.of(authorProject2));

        AuthorRepository authorRepository = Mockito.mock(AuthorRepository.class);
        List<Author> authorList = new ArrayList<>();
        authorList.add(author1);
        authorList.add(author2);
        authorList.add(author3);
        Mockito.when(authorRepository.findAll()).thenReturn(authorList);
        Mockito.when(authorRepository.saveAll(authorList)).thenReturn(authorList);

        ObjectMapper objectMapper = new ObjectMapper();
        AuthorService authorService = new AuthorService(authorRepository, objectMapper);
        assertThat(authorService.updateAuthor(1L));
        assertThat(author1.getExpertTf() != null && author1.getExpertTf().length() > 1);
        assertThat(author2.getExpertTf()==null);
        assertThat(author3.getExpertTf()==null);
    }

    @Test
    void testFindPossibleAuthor(){
        Author author1 = new Author();
        author1.setExpertidtk(1L);
        Author author2 = new Author();
        author2.setExpertidtk(2L);
        Author author3 = new Author();
        author3.setExpertidtk(3L);
        Set<AuthorProject> authorProjectSet = new HashSet<>();
        author3.setAuthorProjects(authorProjectSet);
        Project project1 = new Project();
        project1.setProjectIdTk(1L);
        project1.setDescEn("simulation of unit tests");
        project1.setNameEn("Project number one");
        project1.setKeywords("simulation");
        Project project2 = new Project();
        project2.setProjectIdTk(2L);
        project2.setDescEn("Cats stuck in trees");
        project2.setNameEn("Project number two");
        project2.setKeywords("cats trees");

        AuthorProject authorProject1 = new AuthorProject();
        authorProject1.setAuthor(author1);
        authorProject1.setProject(project1);
        author1.setAuthorProjects(Set.of(authorProject1));

        AuthorProject authorProject2 = new AuthorProject();
        authorProject2.setAuthor(author2);
        authorProject2.setProject(project2);
        author2.setAuthorProjects(Set.of(authorProject2));

        AuthorRepository authorRepository = Mockito.mock(AuthorRepository.class);
        List<Author> authorList = new ArrayList<>();
        authorList.add(author1);
        authorList.add(author2);
        authorList.add(author3);
        Mockito.when(authorRepository.findAll()).thenReturn(authorList);
        Mockito.when(authorRepository.saveAll(authorList)).thenReturn(authorList);

        ObjectMapper objectMapper = new ObjectMapper();
        AuthorService authorService = new AuthorService(authorRepository, objectMapper);
        ProjectDto projectDto = new ProjectDto();
        projectDto.setKeywords(project1.getKeywords());
        projectDto.setDescEn(project1.getDescEn());
        projectDto.setNameEn(project1.getNameEn());
        Response response = authorService.findPossibleAuthor(projectDto);
        assertThat(response!=null
                && response.getTfTop()!=null
                && response.getTfTop().size()==2
                && response.getTfTop().get(0).getAuthorID()==0L
                && response.getTfTop().get(1).getAuthorID()==1L);
    }
}

package com.maximsachok.authoridentification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ControllerIntegrationTest {
    private String API_PATH="/author-identification";
    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    protected void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void getAuthorsList() throws Exception {
        setUp();
        String uri = API_PATH+"/author";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<AuthorDto> authorList = objectMapper.readValue(content, new TypeReference<List<AuthorDto>>(){});
        assertTrue(authorList.size() > 0);
    }
    @Test
    public void getProjectsList() throws Exception {
        setUp();
        String uri = API_PATH+"/project";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ProjectDto> projectList = objectMapper.readValue(content, new TypeReference<List<ProjectDto>>(){});
        assertTrue(projectList.size() > 0);
    }

    @Test
    public void getAuthorProjectsList() throws Exception {
        setUp();
        ObjectMapper objectMapper = new ObjectMapper();
        String uri = API_PATH+"/author/8/projects";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        List<ProjectDto> projects = objectMapper.readValue(content, new TypeReference<List<ProjectDto>>(){});
        assertEquals(200, status);
        assertTrue(projects.size()>0);
    }


    @Test
    public void getProjectAuthorsList() throws Exception {
        setUp();
        ObjectMapper objectMapper = new ObjectMapper();
        String uri = API_PATH+"/project/8927/authors";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();
        List<AuthorDto> authors = objectMapper.readValue(content, new TypeReference<List<AuthorDto>>(){});
        assertEquals(200, status);
        assertEquals(1, authors.size());
    }

    @Test
    public void createAuthor() throws Exception {
        setUp();
        String uri = API_PATH+"/author";
        ObjectMapper objectMapper = new ObjectMapper();
        AuthorDto authorDto = new AuthorDto();
        String author = objectMapper.writeValueAsString(authorDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON)
                .content(author).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201,status);
        String content = mvcResult.getResponse().getContentAsString();
        Long authorId = objectMapper.readValue(content, new TypeReference<Long>(){});
        assertNotNull(authorId);
    }

    @Test
    public void createProject() throws Exception {
        setUp();
        String uri = API_PATH+"/project";
        ObjectMapper objectMapper = new ObjectMapper();
        ProjectDto projectDto = new ProjectDto();
        projectDto.setDescEn("a");
        projectDto.setKeywords("b");
        projectDto.setNameEn("c");
        String json = objectMapper.writeValueAsString(projectDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON)
                .content(json).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(201,status);
        String content = mvcResult.getResponse().getContentAsString();
        Long projectId = objectMapper.readValue(content, new TypeReference<Long>(){});
        assertNotNull(projectId);
    }

    @Test
    public void updateProject() throws  Exception {
        setUp();
        String uri = API_PATH+"/project/7273";
        ObjectMapper objectMapper = new ObjectMapper();
        ProjectDto projectDto = new ProjectDto();
        projectDto.setDescEn("a");
        projectDto.setKeywords("b");
        projectDto.setNameEn("c");
        projectDto.setId(7273L);
        String json = objectMapper.writeValueAsString(projectDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON)
                .content(json).accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200,status);
    }


    @Test
    public void removeProjectFromAuthor() throws Exception {
        setUp();
        String uri = API_PATH+"/author/8/project/7273";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ProjectDto> projects = objectMapper.readValue(content, new TypeReference<List<ProjectDto>>(){});
        assertEquals(1, projects.size());
    }

    @Test
    public void deleteAuthor() throws Exception {
        setUp();
        String uri = API_PATH+"/author/150";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void deleteProject() throws Exception {
        setUp();
        String uri = API_PATH+"/project/2";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }

    @Test
    public void addProjectToAuthor() throws Exception{
        setUp();
        String uri = API_PATH+"/author/60/project/7273";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
        String content = mvcResult.getResponse().getContentAsString();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ProjectDto> projects = objectMapper.readValue(content, new TypeReference<List<ProjectDto>>(){});
        assertEquals(3, projects.size());
    }

}

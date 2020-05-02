package com.maximsachok.authoridentification;

import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.restcontroller.Controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SmokeTests {

    @Autowired
    Controller controller;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    AuthorProjectRepository authorProjectRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Test
    public void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
        assertThat(authorRepository).isNotNull();
        assertThat(authorProjectRepository).isNotNull();
        assertThat(projectRepository).isNotNull();
    }

}

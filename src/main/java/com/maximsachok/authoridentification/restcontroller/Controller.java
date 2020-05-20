package com.maximsachok.authoridentification.restcontroller;

import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@RestController
public class Controller {

    private AuthorService authorService;
    @Autowired
    public Controller(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/testAlgorithm")
    public ResponseEntity<?> testAlgorithm(){
        return ResponseEntity.ok(authorService.testAlgorithm()*100+"% of success");
    }

    /**
     *Finds the possible author for a given project.
     * @param project Project for which to find an author
     * @return Returns Long id of possible author. If called during update returns "bad request".

     */
    @PostMapping("/find")
    public ResponseEntity<?> find(@Validated @RequestBody ProjectDto project) throws Exception {
        return ResponseEntity.ok(authorService.findPossibleAuthor(project));
    }

}

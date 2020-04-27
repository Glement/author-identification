package com.maximsachok.authoridentification.restcontroller;

import com.maximsachok.authoridentification.dto.Response;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.Result;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@RestController
public class Controller {

    AuthorService authorService;

    @Autowired
    public Controller(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping("/score")
    public ResponseEntity<Response> findScore(@Validated @RequestBody ProjectDto project) {
        Response response;
        response = authorService.findPossibleAuthor(project);
        if(response.getBothTop().size()>0 &&
                response.getWordTop().size()>0 &&
                response.getTfTop().size()>0)
            return ResponseEntity.ok(response);
        return ResponseEntity.of(Optional.empty());
    }
    @PutMapping("/vector-{id}")
    public ResponseEntity<String> updateVector(@RequestParam int authorID) {
        String stud = "";
        return ResponseEntity.ok(stud);
    }
    @GetMapping("/vectorall")
    public ResponseEntity<?> updateAll() {
        return ResponseEntity.ok(authorService.updateAllAuthors());
    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        long startTime = System.currentTimeMillis();
        authorService.findAuthorById(BigInteger.valueOf(10));
        return ResponseEntity.ok(">> Time took to find author "+(System.currentTimeMillis() - startTime)+" ms");
    }

}

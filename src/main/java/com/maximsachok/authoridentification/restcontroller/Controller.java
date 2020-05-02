package com.maximsachok.authoridentification.restcontroller;

import com.maximsachok.authoridentification.dto.Response;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.Result;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@RestController
public class Controller {

    private AuthorService authorService;

    @Autowired
    public Controller(AuthorService authorService) {
        this.authorService = authorService;
    }

    /**
     *Finds the possible author for a given project.
     * @param project Project for which to find an author
     * @return Returns Response object which contains the last 10 authors with the highest similarity scores.
     * @see Response

     */
    @PostMapping("/find")
    public ResponseEntity<Response> findScore(@Validated @RequestBody ProjectDto project) {
        Response response;
        response = authorService.findPossibleAuthor(project);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates vectors for a given author.
     * @param authorID Authors id for which to update vectors.
     * @return If author was found returns status.ok, if not throws an exception.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateVector(@RequestParam long authorID) {
       if(authorService.updateAuthor(authorID))
           return ResponseEntity.ok("Updated");
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Author not found"
        );
    }

    /**
     * Updates vectors of all authors.
     * @return Returns time, how long it took to perform update.
     */
    @GetMapping("/updateall")
    public ResponseEntity<?> updateAll()
    {
        long startTime = System.currentTimeMillis();
        authorService.updateAllAuthors();
        return ResponseEntity.ok("Done in "+(System.currentTimeMillis()-startTime)+" ms");
    }

}

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
import java.util.*;


@RestController
public class Controller {

    private AuthorService authorService;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    public Controller(AuthorService authorService) {
        this.authorService = authorService;
    }

    private List<Project> getProjects(Author author){
        List<Project> projects = new ArrayList<>();
        for(AuthorProject authorProject : author.getAuthorProjects()){
            projects.add(authorProject.getProject());
        }
        return projects;
    }

    @GetMapping("/mostpopular")
    public ResponseEntity<?> findb(){
        double max = 0.0d;
        Author result = null;
        for(Author author : authorRepository.findAll()){
            List<Project> projects = new ArrayList<>();
            projects = getProjects(author);
            if(projects.isEmpty())
                continue;
            if(max<projects.size()){
                max=projects.size();
                result = author;
            }

        }
        assert result != null;
        return ResponseEntity.ok(result.getExpertidtk());
    }

    @GetMapping("/test")
    public ResponseEntity<?> finda(){
        double average = 0.0d;
        double numberOfAuthors = 0.0d;
        Author result = null;
        for(Author author : authorRepository.findAll()){
            List<Project> projects = new ArrayList<>();
            projects = getProjects(author);
            if(projects.isEmpty())
                continue;
            numberOfAuthors++;
            average+=projects.size();
        }
        return ResponseEntity.ok(average/numberOfAuthors);
    }

    @GetMapping("/alg")
    public ResponseEntity<?> alg(){
        return ResponseEntity.ok(authorService.alg()*100+"% of success");
    }

    /*@GetMapping("/test")
    public ResponseEntity<?> testing(){

    }*/

    /**
     *Finds the possible author for a given project.
     * @param project Project for which to find an author
     * @return Returns Response object which contains the last 10 authors with the highest similarity scores.
     * @see Response

     */
    @PostMapping("/find")
    public ResponseEntity<Response> find(@Validated @RequestBody ProjectDto project) {
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
        if(authorService.isUpdating())
            return ResponseEntity.ok("Already updating.");
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
    public ResponseEntity<?> updateAll() {
        if(authorService.isUpdating())
            return ResponseEntity.ok("Already updating.");
        long startTime = System.currentTimeMillis();
        authorService.updateAllAuthors();
        return ResponseEntity.ok("Done in "+(System.currentTimeMillis()-startTime)+" ms");
    }

}

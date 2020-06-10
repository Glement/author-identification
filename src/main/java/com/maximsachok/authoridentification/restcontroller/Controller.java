package com.maximsachok.authoridentification.restcontroller;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.services.AuthorService;
import com.maximsachok.authoridentification.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;


@RestController
public class Controller {

    private AuthorService authorService;
    private ProjectService projectService;
    @Autowired
    public Controller(AuthorService authorService, ProjectService projectService) {
        this.authorService = authorService;
        this.projectService = projectService;
    }

    @GetMapping("/author")
    public ResponseEntity<?> getAuthors(){
        List<AuthorDto> authorDtoList = new ArrayList<>();
        for(Author author : authorService.getAuthors()){
            AuthorDto authorDto = new AuthorDto(author.getExpertidtk());
            authorDtoList.add(authorDto);
        }
        return ResponseEntity.ok(authorDtoList);
    }

    @GetMapping("/project")
    public ResponseEntity<?> getProjects(){
        List<ProjectDto> projectDtoList = new ArrayList<>();
        for(Project project : projectService.getProjects()){
            ProjectDto projectDto = new ProjectDto();
            projectDto.setNameEn(project.getNameEn());
            projectDto.setKeywords(project.getKeywords());
            projectDto.setDescEn(project.getDescEn());
            projectDto.setId(project.getProjectIdTk());
            projectDtoList.add(projectDto);
        }
        return ResponseEntity.ok(projectDtoList);
    }

    @GetMapping("/author/{id}")
    public ResponseEntity<?> getAuthor(@PathVariable Long id){
        if(authorService.getAuthor(id).isPresent()){
            return new ResponseEntity<>(new AuthorDto(authorService.getAuthor(id).get().getExpertidtk()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<?> getProject(@PathVariable Long id){
       if(projectService.getProject(id).isEmpty()){
           ProjectDto projectDto = new ProjectDto();
           projectDto.setNameEn(projectService.getProject(id).get().getNameEn());
           projectDto.setKeywords(projectService.getProject(id).get().getKeywords());
           projectDto.setDescEn(projectService.getProject(id).get().getDescEn());
            return new ResponseEntity<>(projectDto, HttpStatus.OK);
       }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/author/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id){
        if(authorService.deleteAuthor(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/project/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id){
        if(projectService.deleteProject(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/project")
    public ResponseEntity<?> createProject(@Validated @RequestBody ProjectDto project){
        return new ResponseEntity<>(projectService.createProject(project), HttpStatus.OK);
    }

    @PostMapping("/author")
    public ResponseEntity<?> createAuthor(@Validated @RequestBody AuthorDto author){
        return new ResponseEntity<>(authorService.createAuthor(author), HttpStatus.OK);
    }

    @PutMapping("/project/{id}")
    public ResponseEntity<?> updateProject(@Validated @RequestBody ProjectDto projectDto, @PathVariable Long id){
        if(projectService.getProject(id).isPresent()){
            Project project = projectService.getProject(id).get();
            project.setDescEn(projectDto.getDescEn());
            project.setKeywords(projectDto.getKeywords());
            project.setNameEn(projectDto.getNameEn());
            projectService.updateProject(project);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/author/{aid}/project/{pid}")
    public ResponseEntity<?> addProjectToAuthor(@PathVariable Long aid, @PathVariable Long pid){
        if(authorService.getAuthor(aid).isPresent() && projectService.getProject(pid).isPresent()){
            return new ResponseEntity<>(authorService.addProject(authorService.getAuthor(aid).get(),projectService.getProject(pid).get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/author/{aid}/project/{pid}")
    public ResponseEntity<?> deleteProjectFromAuthor(@PathVariable Long aid, @PathVariable Long pid){
        if(authorService.getAuthor(aid).isPresent() && projectService.getProject(pid).isPresent()){
            return new ResponseEntity<>(authorService.removeProject(authorService.getAuthor(aid).get(),projectService.getProject(pid).get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/author/{id}/projects")
    public ResponseEntity<?> getAuthorProjects(@PathVariable Long id){
        List<ProjectDto> projects;
        projects = authorService.getAuthorProjectsDto(id);
        if(projects!=null){
            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/project/{id}/authors")
    public ResponseEntity<?> getProjectAuthors(@PathVariable Long id){
        List<AuthorDto> authors;
        authors = projectService.getProjectAuthors(id);
        if(authors!=null){
            return new ResponseEntity<>(authors, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

    @GetMapping("/test-algorithm")
    public ResponseEntity<?> testAlgorithm(){
        return ResponseEntity.ok(authorService.testAlgorithm()*100+"% of success");
    }

}

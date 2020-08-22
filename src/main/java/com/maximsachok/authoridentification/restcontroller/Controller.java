package com.maximsachok.authoridentification.restcontroller;


import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.services.AuthorService;
import com.maximsachok.authoridentification.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Main Rest Controller, controls all incoming and outgoing HTTP requests and responses
 */
@RestController
@RequestMapping("/author-identification")
public class Controller {

    private AuthorService authorService;
    private ProjectService projectService;
    @Autowired
    public Controller(AuthorService authorService, ProjectService projectService) {
        this.authorService = authorService;
        this.projectService = projectService;
    }

    /**
     *
     * @return List of AuthorDto with status code OK
     */
    @GetMapping("/author")
    public ResponseEntity<?> getAuthors(){
        List<AuthorDto> authorDtoList = new ArrayList<>();
        for(Author author : authorService.getAuthors()){
            AuthorDto authorDto = new AuthorDto(author.getExpertidtk());
            authorDtoList.add(authorDto);
        }
        return ResponseEntity.ok(authorDtoList);
    }

    /**
     *
     * @return List of ProjectDto with status code OK
     */
    @GetMapping("/project")
    public ResponseEntity<?> getProjects(){
        List<ProjectDto> projectDtoList = new ArrayList<>();
        for(Project project : projectService.getProjects()){
            projectDtoList.add(ProjectService.projectToProjectDto(project));
        }
        return ResponseEntity.ok(projectDtoList);
    }

    /**
     *
     * @param id author id
     * @return AuthorDto with status code OK or status code NOT_FOUND if author not found
     */
    @Transactional
    @GetMapping("/author/{id}")
    public ResponseEntity<?> getAuthor(@PathVariable Long id){
        if(authorService.getAuthor(id).isPresent()){
            return new ResponseEntity<>(new AuthorDto(authorService.getAuthor(id).get().getExpertidtk()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param id project id
     * @return ProjectDto with status code OK or status code NOT_FOUND if project not found
     */
    @Transactional
    @GetMapping("/project/{id}")
    public ResponseEntity<?> getProject(@PathVariable Long id){
        Optional<Project> project = projectService.getProject(id);
       if(project.isPresent()){
            return new ResponseEntity<>(ProjectService.projectToProjectDto(project.get()), HttpStatus.OK);
       }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param id author id
     * @return status code OK if deleted, NOT_FOUND is author not found
     */
    @Transactional
    @DeleteMapping("/author/{id}")
    public ResponseEntity<?> deleteAuthor(@PathVariable Long id){
        if(authorService.deleteAuthor(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param id project id
     * @return status code OK if deleted, NOT_FOUND is project not found
     */
    @Transactional
    @DeleteMapping("/project/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id){
        if(projectService.deleteProject(id)){
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param project ProjectDto
     * @return Long id of created project with status code CREATED
     */
    @PostMapping("/project")
    public ResponseEntity<?> createProject(@Validated @RequestBody ProjectDto project){
        return new ResponseEntity<>(projectService.createProject(project), HttpStatus.CREATED);
    }

    /**
     *
     * @param author AuthorDto
     * @return Long id of created author with status code CREATED
     */
    @PostMapping("/author")
    public ResponseEntity<?> createAuthor(@Validated @RequestBody AuthorDto author){
        return new ResponseEntity<>(authorService.createAuthor(author), HttpStatus.CREATED);
    }

    /**
     *
     * @param projectDto new project data
     * @param id projects id
     * @return either the updated project and OK status or NOT_FOUND status if project not found.
     */
    @Transactional
    @PutMapping("/project/{id}")
    public ResponseEntity<?> updateProject(@Validated @RequestBody ProjectDto projectDto, @PathVariable Long id){
        if(projectService.getProject(id).isPresent()){
            Project project = projectService.getProject(id).get();
            project.setDescEn(projectDto.getDescEn());
            project.setKeywords(projectDto.getKeywords());
            project.setNameEn(projectDto.getNameEn());
            projectDto.setId(id);
            projectService.updateProject(project);
            return new ResponseEntity<>(projectDto, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param aid author id
     * @param pid project id
     * @return list of author projects with status OK or status NOT_FOUND if author or project are not found
     */
    @Transactional
    @PutMapping("/author/{aid}/project/{pid}")
    public ResponseEntity<?> addProjectToAuthor(@PathVariable Long aid, @PathVariable Long pid){
        if(authorService.getAuthor(aid).isPresent() && projectService.getProject(pid).isPresent()){
            return new ResponseEntity<>(authorService.addProject(authorService.getAuthor(aid).get(),projectService.getProject(pid).get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param aid author id
     * @param pid project id
     * @return list of project authors with status OK or status NOT_FOUND if author or project are not found
     */
    @Transactional
    @PutMapping("/project/{pid}/author/{aid}")
    public ResponseEntity<?> addAuthorToProject(@PathVariable Long aid, @PathVariable Long pid){
        if(authorService.getAuthor(aid).isPresent() && projectService.getProject(pid).isPresent()){
            return new ResponseEntity<>(projectService.addAuthor(authorService.getAuthor(aid).get(),projectService.getProject(pid).get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param aid author id
     * @param pid project id
     * @return list of author projects with status OK or status NOT_FOUND if author or project are not found
     */
    @Transactional
    @DeleteMapping("/author/{aid}/project/{pid}")
    public ResponseEntity<?> deleteProjectFromAuthor(@PathVariable Long aid, @PathVariable Long pid){
        if(authorService.getAuthor(aid).isPresent() && projectService.getProject(pid).isPresent()){
            return new ResponseEntity<>(authorService.removeProject(authorService.getAuthor(aid).get(),projectService.getProject(pid).get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param aid author id
     * @param pid project id
     * @return list of project authors with status OK or status NOT_FOUND if author or project are not found
     */
    @Transactional
    @DeleteMapping("/project/{pid}/author/{aid}")
    public ResponseEntity<?> deleteAuthorFromProject(@PathVariable Long aid, @PathVariable Long pid){
        if(authorService.getAuthor(aid).isPresent() && projectService.getProject(pid).isPresent()){
            return new ResponseEntity<>(projectService.removeAuthor(authorService.getAuthor(aid).get(),projectService.getProject(pid).get()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param id author id
     * @return list of projects for given author with status OK or status NOT_FOUND if author not found
     */
    @Transactional
    @GetMapping("/author/{id}/projects")
    public ResponseEntity<?> getAuthorProjects(@PathVariable Long id){
        Optional<List<ProjectDto>> projects;
        projects = authorService.getAuthorProjectsDto(id);
        if(projects.isPresent()){
            return new ResponseEntity<>(projects, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     *
     * @param id project id
     * @return list of authors for given project with status OK or status NOT_FOUND if project not found
     */
    @Transactional
    @GetMapping("/project/{id}/authors")
    public ResponseEntity<?> getProjectAuthors(@PathVariable Long id){
        Optional<List<AuthorDto>> authors;
        authors = projectService.getProjectAuthors(id);
        if(authors.isPresent()){
            return new ResponseEntity<>(authors.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Tests the algorithm.
     * @return either ACCEPTED status code if classifier isn't ready for testing yet or accuracy in percent in Double with OK status code
     */
    @GetMapping("/test-algorithm")
    public ResponseEntity<?> testAlgorithm(){
        if(!authorService.isClassifierInitialized()){
            Thread initClassifier = new Thread(() -> authorService.initClassifier());
            initClassifier.start();
            return new ResponseEntity<>((double) 0, HttpStatus.ACCEPTED);
        }
        return ResponseEntity.ok(authorService.testAlgorithm()*100);
    }


    /**
     * Find the most possible author for the given text
     * @param project ProjectDto
     * @return List of SearchResultDto sorted descending by the score. Each entry contains author and a score. Higher the score the more probable this is the author of the given text
     */
    @PostMapping("/find")
    public ResponseEntity<?> find(@Validated @RequestBody ProjectDto project){
        if(!authorService.isClassifierInitialized()){
            Thread initClassifier = new Thread(() -> authorService.initClassifier());
            initClassifier.start();
            return new ResponseEntity<>((double) 0, HttpStatus.ACCEPTED);
        }
        return ResponseEntity.ok(authorService.findPossibleAuthor(project));
    }

    /**
     * Refreshes classifier
     * @return ACCEPTED status code.
     */
    @GetMapping("/refresh-classifier")
    public ResponseEntity<?> refresh(){

        Thread refreshClassifier = new Thread(() ->  {
        if(!authorService.isClassifierInitialized())
            authorService.initClassifier();
        else
            authorService.refreshClassifier();});
        refreshClassifier.start();
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}

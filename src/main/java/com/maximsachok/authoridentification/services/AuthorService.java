package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.dto.AuthorDto;
import com.maximsachok.authoridentification.dto.ProjectDto;
import com.maximsachok.authoridentification.dto.SearchResultDto;
import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.AuthorProjectCompositeId;
import com.maximsachok.authoridentification.entitys.Project;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import com.maximsachok.authoridentification.repositorys.AuthorRepository;
import com.maximsachok.authoridentification.repositorys.ProjectRepository;
import com.maximsachok.authoridentification.textvectorization.AuthorClassifierWrapper;
import com.maximsachok.authoridentification.textvectorization.WekaClassifier;
import com.maximsachok.authoridentification.textvectorization.WekaClassifierWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class AuthorService {

    private AuthorRepository authorRepository;
    private ProjectRepository projectRepository;
    private AuthorProjectRepository authorProjectRepository;
    private static final AuthorClassifierWrapper authorClassifier = new WekaClassifierWrapper();

    @Autowired
    public AuthorService(AuthorRepository authorRepository, ProjectRepository projectRepository, AuthorProjectRepository authorProjectRepository) {
        this.authorRepository = authorRepository;
        this.projectRepository = projectRepository;
        this.authorProjectRepository = authorProjectRepository;
    }

    public static AuthorDto AuthorToAuthorDto(Author author) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setId(author.getExpertidtk());
        return authorDto;
    }

    public List<Author> getAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthor(Long id) {
        return authorRepository.findById(id);
    }

    public boolean deleteAuthor(Long id) {
        if (authorRepository.findById(id).isPresent()) {
            authorRepository.delete(authorRepository.findById(id).get());
            return true;
        }
        return false;
    }


    public List<ProjectDto> removeProject(Author author, Project project) {
        List<ProjectDto> projects = new ArrayList<>();
        for (AuthorProject authorProject : author.getAuthorProjects()) {
            if (authorProject.getProject().getProjectIdTk().equals(project.getProjectIdTk())) {
                authorProjectRepository.delete(authorProject);
            } else
                projects.add(ProjectService.projectToProjectDto(authorProject.getProject()));
        }
        return projects;
    }

    public Long createAuthor(AuthorDto author) {
        Author author1 = new Author();
        return authorRepository.saveAndFlush(author1).getExpertidtk();
    }


    /**
     * Adds project to author projects
     *
     * @param author author to which to add project
     * @param project project to add
     * @return
     */
    public Optional<List<ProjectDto>> addProject(Author author, Project project) {
        AuthorProject authorProject = new AuthorProject();
        authorProject.setProject(project);
        authorProject.setAuthor(author);
        AuthorProjectCompositeId authorProjectCompositeId = new AuthorProjectCompositeId();
        authorProjectCompositeId.setAuthor(author.getExpertidtk());
        authorProjectCompositeId.setProject(project.getProjectIdTk());
        if (authorProjectRepository.findById(authorProjectCompositeId).isEmpty()) {
            authorProject = authorProjectRepository.save(authorProject);
            author.getAuthorProjects().add(authorProject);
            authorRepository.save(author);
            project.getAuthorProjects().add(authorProject);
            projectRepository.save(project);
        }
        return getAuthorProjectsDto(author.getExpertidtk());
    }

    public Optional<List<ProjectDto>> getAuthorProjectsDto(Long id) {
        if (authorRepository.findById(id).isEmpty()) {
            return Optional.empty();
        }
        List<ProjectDto> projects = new ArrayList<>();
        for (AuthorProject authorProject : authorRepository.findById(id).get().getAuthorProjects()) {
            projects.add(ProjectService.projectToProjectDto(authorProject.getProject()));
        }
        return Optional.of(projects);
    }

    public double testAlgorithm() {
        return authorClassifier.testAlgorithm();
    }

    /**
     * Looks through all authors and finds possible author that could have written given project, if author already have this project it is excluded from search.
     *
     * @param project Project for which to find possible author
     * @return List of  SearchResultDto, contains AuthorDto and its score, of possible authors for given text, default size is 10, can be smaller if there isn't enough authors in database
     */
    public List<SearchResultDto> findPossibleAuthor(ProjectDto project) {
        return authorClassifier.findPossibleAuthor(project);
    }

    public void initClassifier() {
        authorClassifier.initClassifier(authorRepository.findAll(), new WekaClassifier());
    }

    public void refreshClassifier() {
        authorClassifier.refreshClassifier(authorRepository.findAll());
    }

    public Boolean isClassifierInitialized() {
        return authorClassifier.isInitialized();
    }

    public Boolean isClassifierInitializing() {
        return authorClassifier.isInitializing();
    }


    public Boolean isClassifierRefreshRequested() {
        return authorClassifier.isRefreshRequested();
    }
}

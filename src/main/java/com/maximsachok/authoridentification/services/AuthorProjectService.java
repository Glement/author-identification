package com.maximsachok.authoridentification.services;

import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.AuthorProjectCompositeId;
import com.maximsachok.authoridentification.repositorys.AuthorProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorProjectService {

    private AuthorProjectRepository authorProjectRepository;

    @Autowired
    public AuthorProjectService(AuthorProjectRepository authorProjectRepository) {
        this.authorProjectRepository = authorProjectRepository;
    }

    public void save (AuthorProject authorProject){
        authorProjectRepository.save(authorProject);
    }
}

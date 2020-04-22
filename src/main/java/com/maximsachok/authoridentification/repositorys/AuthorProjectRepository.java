package com.maximsachok.authoridentification.repositorys;

import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.AuthorProjectCompositeId;
import org.springframework.data.repository.CrudRepository;

public interface AuthorProjectRepository extends CrudRepository<AuthorProject, AuthorProjectCompositeId> {
}
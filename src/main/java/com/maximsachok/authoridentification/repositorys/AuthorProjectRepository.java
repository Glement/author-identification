package com.maximsachok.authoridentification.repositorys;

import com.maximsachok.authoridentification.entitys.AuthorProject;
import com.maximsachok.authoridentification.entitys.AuthorProjectCompositeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorProjectRepository extends JpaRepository<AuthorProject, AuthorProjectCompositeId> {
}
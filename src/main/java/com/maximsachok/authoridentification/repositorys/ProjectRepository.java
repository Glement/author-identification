package com.maximsachok.authoridentification.repositorys;

import com.maximsachok.authoridentification.entitys.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Collection;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
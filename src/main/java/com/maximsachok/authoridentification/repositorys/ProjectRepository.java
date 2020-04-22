package com.maximsachok.authoridentification.repositorys;

import com.maximsachok.authoridentification.entitys.Project;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Collection;

public interface ProjectRepository extends CrudRepository<Project, BigInteger> {
}
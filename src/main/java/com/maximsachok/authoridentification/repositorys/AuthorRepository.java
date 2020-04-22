package com.maximsachok.authoridentification.repositorys;

import com.maximsachok.authoridentification.entitys.Author;
import com.maximsachok.authoridentification.entitys.AuthorProjectCompositeId;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface AuthorRepository extends CrudRepository<Author, BigInteger> {
}

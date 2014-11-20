package net.datenstrudel.bulbs.core.infrastructure.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by Thomas Wendzinski.
 */
@NoRepositoryBean
public interface BCoreRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {

}

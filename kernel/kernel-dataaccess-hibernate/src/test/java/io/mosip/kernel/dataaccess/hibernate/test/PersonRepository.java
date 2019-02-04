package io.mosip.kernel.dataaccess.hibernate.test;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.dataaccess.hibernate.test.model.Person;


/**
 * This interface extends BaseRepository which provides with the methods for
 * several CRUD operations.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Repository
public interface PersonRepository extends BaseRepository<Person, Integer> {
}

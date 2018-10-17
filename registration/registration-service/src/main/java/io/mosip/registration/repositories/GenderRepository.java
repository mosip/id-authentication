package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.GenericId;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@Repository
public interface GenderRepository extends BaseRepository<Gender, GenericId> {

}

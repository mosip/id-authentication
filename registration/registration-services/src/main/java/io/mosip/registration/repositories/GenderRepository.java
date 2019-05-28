package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.Gender;

/**
 * Interface for {@link Gender}
 * 
 * @author Brahmananda Reddy
 *
 */

public interface GenderRepository extends BaseRepository<Gender, String> {
	List<Gender> findByIsActiveTrueAndLangCode(String langCode);
	List<Gender> findAllByIsActiveTrue();
}

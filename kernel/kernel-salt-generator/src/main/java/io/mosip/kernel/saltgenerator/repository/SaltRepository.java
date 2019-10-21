package io.mosip.kernel.saltgenerator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.kernel.saltgenerator.entity.SaltEntity;

/**
 * The Interface SaltRepository.
 *
 * @author Manoj SP
 */
public interface SaltRepository extends JpaRepository<SaltEntity, Long> {

	/**
	 * Count by id in list of ids.
	 *
	 * @param ids the ids
	 * @return the long
	 */
	Long countByIdIn(List<Long> ids);
}

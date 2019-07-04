package io.mosip.idrepository.saltgenerator.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.mosip.idrepository.saltgenerator.entity.SaltEntity;

/**
 * @author Manoj SP
 *
 */
public interface SaltRepository extends JpaRepository<SaltEntity, Long> {

	Long countByIdIn(List<Long> ids);
}

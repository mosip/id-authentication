package io.mosip.authentication.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.VIDEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface VIDRepository is used to fetch VIDEntity.
 *  @author Rakesh Roshan
 */
@Repository
public interface VIDRepository extends BaseRepository<VIDEntity, String> {
	
	@Query("Select uin from VIDEntity where id = :vidNumber")
	Optional<String> findUinByVid(@Param("vidNumber") String uinRefId);
}

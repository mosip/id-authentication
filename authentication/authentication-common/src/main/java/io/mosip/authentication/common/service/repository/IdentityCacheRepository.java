package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

@Repository
public interface IdentityCacheRepository extends BaseRepository<IdentityEntity, String> {

	/**
	 * Fetch only required fields for demo data by Id.
	 * Avoids fetching unnecessary large objects.
	 */
	@Query("SELECT i.id, i.demographicData, i.expiryTimestamp, i.transactionLimit, i.token, " +
			"i.crBy, i.crDTimes, i.updBy, i.updDTimes, i.isDeleted, i.delDTimes " +
			"FROM IdentityEntity i WHERE i.id = :id")
	Optional<Object[]> findDemoDataById(@Param("id") String id);

	/**
	 * Fetch only transaction limit by Id.
	 */
	@Query("SELECT i.id, i.expiryTimestamp, i.transactionLimit FROM IdentityEntity i WHERE i.id = :id")
	Optional<Object[]> findTransactionLimitById(@Param("id") String id);

	/**
	 * Existence check optimized for boolean return to avoid unnecessary object hydration.
	 */
	@Query("SELECT 1 FROM IdentityEntity i WHERE i.id = :id")
	boolean existsById(String id);
}
package io.mosip.authentication.common.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.IdentityEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * Repository class for Identity Cache table
 * 
 * @author Loganathan Sekar
 *
 */
@Repository
public interface IdentityCacheRepository extends BaseRepository<IdentityEntity, String> {

	@Query("SELECT i.id, i.demographicData, i.expiryTimestamp, i.transactionLimit, i.token, i.crBy, i.crDTimes, "
			+ "i.updBy, i.updDTimes, i.isDeleted, i.delDTimes FROM IdentityEntity i where i.id = :id")
	List<Object[]> findDemoDataById(@Param("id") String id);

	@Query("SELECT i.id, i.expiryTimestamp, i.transactionLimit "
			+ " FROM IdentityEntity i where i.id = :id")
	List<Object[]> findTransactionLimitById(@Param("id") String id);
}

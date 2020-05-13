package io.mosip.authentication.common.service.repository;

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

	@Query("SELECT i.id, i.demo_data, i.expiry_timestamp, i.transaction_limit, i.cr_by, i.cr_dtimes, "
			+ "i.upd_by, i.upd_dtimes, i.is_deleted, i.del_dtimes FROM IdentityCache i")
	IdentityEntity findDemoDataById(@Param("id") String id);
}

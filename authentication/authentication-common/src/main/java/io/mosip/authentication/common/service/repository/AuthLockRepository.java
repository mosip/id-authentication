package io.mosip.authentication.common.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.AuthtypeLock;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * 
 * @author Dinesh Karuppiah.T
 *
 */
@Repository
public interface AuthLockRepository extends BaseRepository<AuthtypeLock, Integer> {

	@Query(value = "Select * from ida.uin_auth_lock where uin=:individualId ORDER BY cr_dtimes DESC", nativeQuery = true)
	public List<AuthtypeLock> findByUin(@Param("individualId") String individualId);

}

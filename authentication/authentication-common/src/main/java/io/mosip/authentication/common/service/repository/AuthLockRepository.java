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

	@Query(value = "select t.* " + 
			"from  ida.uin_auth_lock t " + 
			"inner join ( " + 
			"    select auth_type_code, MAX(cr_dtimes) as crd " + 
			"    from ida.uin_auth_lock " + 
			"    group by uin_hash, auth_type_code " + 
			") tm on t.auth_type_code = tm.auth_type_code and t.cr_dtimes = tm.crd " + 
			"where t.uin = :uin", 
			nativeQuery = true)
	public List<AuthtypeLock> findByUin(@Param("uin") String uin);

}	

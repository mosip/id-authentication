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

	@Query(value = "select " + 
			"        t.auth_type_code, " + 
			"        t.status_code,  " + 
			"        t.unlock_expiry_datetime  " + 
			"    from " + 
			"        ida.uin_auth_lock t  " + 
			"    inner join " + 
			"        ( " + 
			"            select " + 
			"                auth_type_code, " + 
			"                MAX(cr_dtimes) as crd " + 
			"            from " + 
			"                ida.uin_auth_lock      " + 
			"            where " + 
			"                token_id = :token_id " + 
			"            group by " + 
			"                token_id, " + 
			"                auth_type_code  " + 
			"        ) tm  " + 
			"            on t.auth_type_code = tm.auth_type_code  " + 
			"            and t.cr_dtimes = tm.crd  " + 
			"    where " + 
			"        t.token_id = :token_id", 
			nativeQuery = true)
	public List<Object[]> findByToken(@Param("token_id") String tokenId);
	
	public List<AuthtypeLock> findByTokenAndAuthtypecode(String tokenId, String authtypecode);

}	

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

    @Query(value =
            "SELECT " +
                    "    t.auth_type_code, " +
                    "    t.status_code, " +
                    "    t.unlock_expiry_datetime " +
                    "FROM (" +
                    "    SELECT " +
                    "        auth_type_code, " +
                    "        status_code, " +
                    "        unlock_expiry_datetime, " +
                    "        ROW_NUMBER() OVER (PARTITION BY token_id, auth_type_code ORDER BY cr_dtimes DESC) as rn " +
                    "    FROM ida.uin_auth_lock " +
                    "    WHERE token_id = :token_id " +
                    ") t " +
                    "WHERE t.rn = 1",
            nativeQuery = true)
    public List<Object[]> findByToken(@Param("token_id") String tokenId);
	
	public List<AuthtypeLock> findByTokenAndAuthtypecode(String tokenId, String authtypecode);

}	

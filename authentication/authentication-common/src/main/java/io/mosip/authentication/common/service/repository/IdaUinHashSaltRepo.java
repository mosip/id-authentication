package io.mosip.authentication.common.service.repository;
import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UIN_HASH_SALT;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.IdaUinHashSalt;

/**
 * The Interface IdaUinHashSaltRepo.
 *
 * @author Arun Bose S
 */
@Repository
public interface IdaUinHashSaltRepo extends JpaRepository<IdaUinHashSalt, Integer> {
	
	/**
	 * The Query to retrieve salt by passing id as parameter.
	 *
	 * @param id the id
	 * @return String salt
	 */

	@Cacheable(value = UIN_HASH_SALT, unless = "#result == null")
	@Query("select salt from IdaUinHashSalt where id = :id")
	public String retrieveSaltById(@Param("id") Integer id);
}

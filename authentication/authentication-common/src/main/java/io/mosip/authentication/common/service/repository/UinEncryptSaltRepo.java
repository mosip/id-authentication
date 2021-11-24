package io.mosip.authentication.common.service.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.UinEncryptSalt;


/**
 * The Interface UinEncryptSaltRepo.
 *
 * @author Arun Bose S
 */
@Repository
public interface UinEncryptSaltRepo extends JpaRepository<UinEncryptSalt, Integer>{
	
	/**
	 * The Query to retrieve salt by passing id as parameter.
	 *
	 * @param id the id
	 * @return String salt
	 */
	@Cacheable(cacheNames = "uin_encrypt_salt")
	@Query("select salt from UinEncryptSalt where id = :id")
	public String retrieveSaltById(@Param("id") Integer id);
}

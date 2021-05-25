package io.mosip.authentication.common.service.repository;

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
	@Query("select salt from IdaUinHashSalt where id = :id")
	public String retrieveSaltById(@Param("id") Integer id);
}

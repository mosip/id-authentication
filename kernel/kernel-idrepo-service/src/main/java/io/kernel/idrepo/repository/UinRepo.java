package io.kernel.idrepo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.kernel.idrepo.entity.Uin;

/**
 * The Interface UinRepo.
 *
 * @author Manoj SP
 */
public interface UinRepo extends JpaRepository<Uin, String> {
	
	/**
	 * Gets the uin ref id by uin.
	 *
	 * @param uin the uin
	 * @return the uin ref id by uin
	 */
	@Query("select uinRefId from Uin where uin = :uin")
	String getUinRefIdByUin(@Param("uin") String uin);
	
	/**
	 * Find by uin.
	 *
	 * @param uin the uin
	 * @return the uin
	 */
	Uin findByUin(String uin);

	/**
	 * Exists by uin.
	 *
	 * @param uin the uin
	 * @return true, if successful
	 */
	boolean existsByUin(String uin);

	/**
	 * Gets the status by uin.
	 *
	 * @param uin the uin
	 * @return the status by uin
	 */
	@Query("select statusCode from Uin where uin = :uin")
	String getStatusByUin(@Param("uin") String uin);
}

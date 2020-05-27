package io.mosip.authentication.common.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.DataEncryptKeystore;

/**
 * @author Manoj SP
 *
 */
@Repository
public interface DataEncryptKeystoreRepository extends JpaRepository<DataEncryptKeystore, Integer>{

	@Query("SELECT d.key from DataEncryptKeystore d where d.id = :id")
	String findKeyById(@Param("id") Integer id);

	@Query("SELECT d.id from DataEncryptKeystore d where d.keyStatus = :status")
	List<Integer> getIdsByKeyStatus(@Param("status") String status);
}

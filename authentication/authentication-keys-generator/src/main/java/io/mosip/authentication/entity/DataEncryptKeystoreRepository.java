package io.mosip.authentication.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataEncryptKeystoreRepository extends JpaRepository<DataEncryptKeystore, Integer>{

	@Query("SELECT d.key from DataEncryptKeystore d where d.id = :id")
	String findKeyById(@Param("id") Integer id);
	
	@Query("SELECT MAX(d.id) from DataEncryptKeystore d")
	Long findMaxId();
}

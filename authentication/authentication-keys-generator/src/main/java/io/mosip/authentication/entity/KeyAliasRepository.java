package io.mosip.authentication.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyAliasRepository extends JpaRepository<KeyAlias, String> {
	List<KeyAlias> findByApplicationIdAndReferenceId(String var1, String var2);
}
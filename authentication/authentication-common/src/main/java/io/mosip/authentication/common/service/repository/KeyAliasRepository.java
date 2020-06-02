package io.mosip.authentication.common.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.KeyAlias;



/**
 * This interface extends BaseRepository which provides with the methods for
 * several CRUD operations.
 * 
 * @author Nagarjuna
 * @since 1.0.0
 *
 */
@Repository
public interface KeyAliasRepository extends JpaRepository<KeyAlias, String> {

	/**
	 * Function to find keyalias by applicationId and referenceId
	 * 
	 * @param applicationId applicationId
	 * @param referenceId   referenceId
	 * @return list of keyalias
	 */
	List<KeyAlias> findByApplicationIdAndReferenceId(String applicationId, String referenceId);
}

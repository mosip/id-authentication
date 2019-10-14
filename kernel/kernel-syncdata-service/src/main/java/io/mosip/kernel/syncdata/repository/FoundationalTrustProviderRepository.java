/**
 * 
 */
package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.syncdata.entity.FoundationalTrustProvider;

/**
 * @author Ramadurai Pandian
 *
 */
@Repository
public interface FoundationalTrustProviderRepository extends JpaRepository<FoundationalTrustProvider, String> {

	// @Query("FROM foundational_trust_provider ftp WHERE ftp.name = ?1 and
	// ftp.email=?2 and ftp.address=?3 and ftp.certAlias=?4 and (ftp.isDeleted is
	// null or ftp.isDeleted =false) AND ftp.isActive = true")
	// FoundationalTrustProvider findByDetails(String name, String email, String
	// address, String certAlias);

	@Query("FROM FoundationalTrustProvider WHERE (createdDateTime > ?1 AND createdDateTime <=?2) OR (updatedDateTime > ?1 AND updatedDateTime <=?2)  OR (deletedDateTime > ?1 AND deletedDateTime <=?2)")
	List<FoundationalTrustProvider> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated,
			LocalDateTime currentTimeStamp);
}

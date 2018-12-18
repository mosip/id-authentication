package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.Title;
import io.mosip.kernel.syncdata.entity.ValidDocument;
import io.mosip.kernel.syncdata.entity.id.ValidDocumentID;

@Repository
public interface ValidDocumentRepository extends BaseRepository<ValidDocument, ValidDocumentID> {
	/**
	 * Method to find list of Title created , updated or deleted time is greater
	 * than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link Title}
	 */
	@Query("FROM ValidDocument WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<ValidDocument> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
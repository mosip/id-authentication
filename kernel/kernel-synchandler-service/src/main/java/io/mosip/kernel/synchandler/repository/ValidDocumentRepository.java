package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.ValidDocument;
import io.mosip.kernel.synchandler.entity.id.ValidDocumentID;

@Repository
public interface ValidDocumentRepository extends BaseRepository<ValidDocument, ValidDocumentID> {

	@Query("FROM ValidDocument WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<ValidDocument> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}
package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.TemplateType;

@Repository
public interface TemplateTypeRepository extends BaseRepository<TemplateType, String> {
	/**
	 * Method to find list of TemplateType created , updated or deleted time is greater
	 * than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link TemplateType}
	 */
	@Query("FROM TemplateType WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<TemplateType> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}

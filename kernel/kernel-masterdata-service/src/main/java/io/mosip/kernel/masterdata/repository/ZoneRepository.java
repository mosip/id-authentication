package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Zone;
import io.mosip.kernel.masterdata.entity.id.CodeAndLanguageCodeID;

/**
 * Zone Repository
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
public interface ZoneRepository extends BaseRepository<Zone, CodeAndLanguageCodeID> {

	@Query("FROM Zone z WHERE (z.isDeleted IS NULL OR z.isDeleted = false) ")
	public List<Zone> findAllNonDeleted();
}

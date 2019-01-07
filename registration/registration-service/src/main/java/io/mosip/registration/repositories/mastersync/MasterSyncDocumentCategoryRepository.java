package io.mosip.registration.repositories.mastersync;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterDocumentCategory;
import io.mosip.registration.entity.mastersync.id.CodeAndLanguageCodeID;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 *
 */
public interface MasterSyncDocumentCategoryRepository extends BaseRepository<MasterDocumentCategory, CodeAndLanguageCodeID> {

	
}

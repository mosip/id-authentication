package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.entity.ValidDocument;

/**
 * 
 * @author Brahmananda Reddy
 *
 */
@Repository
public interface ValidDocumentRepository extends BaseRepository<ValidDocument, GenericId> {

}

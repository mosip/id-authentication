package io.mosip.registration.repositories;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.entity.DocumentFormat;
import io.mosip.registration.entity.GenericId;
/**
 * 
 * @author Brahmananda Reddy
 *
 */
@Repository
public interface DocumentFormatRepository extends BaseRepository<DocumentFormat, GenericId> {

}

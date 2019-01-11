package io.mosip.registration.repositories.mastersync;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.mastersync.MasterTemplateFileFormat;

/**
 * @author Sreekar Chukka
 * @since 1.0.0
 * 
 */
public interface MasterSyncTemplateFileFormatRepository extends BaseRepository<MasterTemplateFileFormat, String> {

}

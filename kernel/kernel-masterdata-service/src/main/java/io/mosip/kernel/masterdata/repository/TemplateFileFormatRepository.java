package io.mosip.kernel.masterdata.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.TemplateFileFormat;

/**
 * @author Neha
 * @since 1.0.0
 * 
 */
@Repository
public interface TemplateFileFormatRepository extends BaseRepository<TemplateFileFormat, String> {

}

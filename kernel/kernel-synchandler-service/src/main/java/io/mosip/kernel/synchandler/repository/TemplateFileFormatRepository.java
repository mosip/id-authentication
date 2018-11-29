package io.mosip.kernel.synchandler.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.TemplateFileFormat;

@Repository
public interface TemplateFileFormatRepository extends BaseRepository<TemplateFileFormat, String> {

}

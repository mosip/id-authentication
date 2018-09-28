package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.TemplateFileFormat;
import org.mosip.registration.entity.TemplateFileFormatPK;

public interface TemplateFileFormatRepository<P> extends BaseRepository<TemplateFileFormat, TemplateFileFormatPK> {
	List<TemplateFileFormat> findByIsActiveTrue();
}

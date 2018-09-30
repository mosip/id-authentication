package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import org.mosip.registration.entity.TemplateFileFormat;

public interface TemplateFileFormatRepository<P> extends BaseRepository<TemplateFileFormat, TemplateEmbeddedKeyCommonFields> {
	List<TemplateFileFormat> findByIsActiveTrue();
}

package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

import io.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import io.mosip.registration.entity.TemplateFileFormat;

public interface TemplateFileFormatRepository<P> extends BaseRepository<TemplateFileFormat, TemplateEmbeddedKeyCommonFields> {
	List<TemplateFileFormat> findByIsActiveTrue();
}

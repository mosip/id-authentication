package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

import io.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import io.mosip.registration.entity.TemplateType;

public interface TemplateTypeRepository<P> extends BaseRepository<TemplateType, TemplateEmbeddedKeyCommonFields> {
	List<TemplateType> findByIsActiveTrue();
}

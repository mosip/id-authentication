package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import org.mosip.registration.entity.TemplateType;

public interface TemplateTypeRepository<P> extends BaseRepository<TemplateType, TemplateEmbeddedKeyCommonFields> {
	List<TemplateType> findByIsActiveTrue();
}

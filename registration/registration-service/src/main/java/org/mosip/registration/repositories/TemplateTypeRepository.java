package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.TemplateType;
import org.mosip.registration.entity.TemplateTypePK;

public interface TemplateTypeRepository<P> extends BaseRepository<TemplateType, TemplateTypePK> {
	List<TemplateType> findByIsActiveTrue();
}

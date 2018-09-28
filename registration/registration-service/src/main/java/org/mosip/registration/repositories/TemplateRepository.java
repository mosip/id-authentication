package org.mosip.registration.repositories;

import java.util.List;

import org.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import org.mosip.registration.entity.Template;

public interface TemplateRepository<P> extends BaseRepository<Template, String>{
	List<Template> findByIsActiveTrue();
}

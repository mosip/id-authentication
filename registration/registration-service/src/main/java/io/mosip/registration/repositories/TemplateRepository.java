package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;

import io.mosip.registration.entity.Template;

public interface TemplateRepository<P> extends BaseRepository<Template, String>{
	List<Template> findByIsActiveTrue();
}

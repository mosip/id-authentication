package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.Template;

/**
 * Repository for Template.
 *
 * @author Himaja Dhanyamraju
 */
public interface TemplateRepository<P> extends BaseRepository<Template, String>{
	/**
	 * This method returns the list of {@link Template} which are active
	 * 
	 * @return the list of {@link Template}
	 */
	List<Template> findByIsActiveTrue();
	
}

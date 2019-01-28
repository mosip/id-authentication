package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import io.mosip.registration.entity.TemplateFileFormat;

/**
 * Repository for TemplateFileFormat.
 *
 * @author Himaja Dhanyamraju
 */
public interface TemplateFileFormatRepository<P> extends BaseRepository<TemplateFileFormat, TemplateEmbeddedKeyCommonFields> {
	
	/**
	 * This method returns the list of {@link TemplateFileFormat} which are active
	 * 
	 * @return the list of {@link TemplateFileFormat}
	 */
	List<TemplateFileFormat> findByIsActiveTrue();
}

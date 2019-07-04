package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.TemplateEmbeddedKeyCommonFields;
import io.mosip.registration.entity.TemplateType;

/**
 * Repository for TemplateType.
 *
 * @author Himaja Dhanyamraju
 */
public interface TemplateTypeRepository<P> extends BaseRepository<TemplateType, TemplateEmbeddedKeyCommonFields> {

	/**
	 * This method returns the list of {@link TemplateType} which are active and
	 * have specified templateTypeCode and language code
	 * 
	 * @param code
	 *            the templateTypeCode
	 * @param langCode
	 * 				the language code required to generate the template
	 * @return the list of {@link TemplateType}
	 */
	List<TemplateType> findByIsActiveTrueAndPkTmpltCodeCodeAndPkTmpltCodeLangCode(String code, String langCode);
}

package io.mosip.kernel.masterdata.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.repository.LanguageRepository;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@Component
public class LanguageUtils {

	/**
	 * Repository used for CRUD operation.
	 */
	@Autowired
	private LanguageRepository languageRepository;

	/**
	 * Checks the given code in the system if present return <b>true</b> else
	 * <b>false</b>otherwise.
	 * 
	 * @param code
	 *            is the provided language code as a request.
	 * @return true if given <code>code</code> is a valid language code.
	 */
	public boolean isValid(String code) {
		if (EmptyCheckUtils.isNullEmpty(code)) {
			return false;
		}
		Language language = languageRepository.findLanguageByCode(code);

		return !EmptyCheckUtils.isNullEmpty(language);
	}

}

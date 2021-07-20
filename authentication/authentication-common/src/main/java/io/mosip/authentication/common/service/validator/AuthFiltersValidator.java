package io.mosip.authentication.common.service.validator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.factory.MosipAuthFilterFactory;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;


/**
 * The Class AuthFiltersValidator - Validator to sequentially validate with the
 * authentication filters
 * 
 * @author Loganathan Sekar
 */
@Component
public class AuthFiltersValidator {
	
	/** The mosip auth filter factory. */
	@Autowired
	private MosipAuthFilterFactory mosipAuthFilterFactory;
	
	/**
	 * Validate auth filters.
	 *
	 * @param authRequestDto the auth request dto
	 * @param identityData the identity data
	 * @param properties the properties
	 * @throws IdAuthenticationFilterException the id authentication filter exception
	 */
	public void validateAuthFilters(AuthRequestDTO authRequestDto, 
			           Map<String, List<IdentityInfoDTO>> identityData,
			           Map<String, Object> properties) throws IdAuthenticationFilterException {
		List<IMosipAuthFilter> enabledAuthFilters = mosipAuthFilterFactory.getEnabledAuthFilters();
		for (IMosipAuthFilter authFilter : enabledAuthFilters) {
			// This will run auth filter validate one by one and any exception thrown from
			// one filter will skip the execution of the rest.
			authFilter.validate(authRequestDto, identityData, properties);
		}
	}

}

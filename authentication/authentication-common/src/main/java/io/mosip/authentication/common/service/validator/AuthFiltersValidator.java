package io.mosip.authentication.common.service.validator;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	// External (partner-facing /auth, /kyc*, /identity-key-binding) and internal
	// (/internal/auth) requests run different filter chains (e.g. partner/hotlist
	// filters only apply externally) - both AuthFilterFactory and
	// InternalAuthFilterFactory are MosipAuthFilterFactory beans in the merged
	// app, so the correct one must be selected per-call via isExternalAuth
	// rather than autowired as a single ambiguous MosipAuthFilterFactory.
	/** The external mosip auth filter factory. */
	@Autowired
	@Qualifier("authFilterFactory")
	private MosipAuthFilterFactory externalAuthFilterFactory;

	/** The internal mosip auth filter factory. */
	@Autowired
	@Qualifier("internalAuthFilterFactory")
	private MosipAuthFilterFactory internalAuthFilterFactory;

	/**
	 * Validate auth filters.
	 *
	 * @param authRequestDto the auth request dto
	 * @param identityData the identity data
	 * @param properties the properties
	 * @param isExternalAuth whether this is an external (partner-facing) auth request,
	 *                       as opposed to an internal auth request - selects which
	 *                       filter chain to run.
	 * @throws IdAuthenticationFilterException the id authentication filter exception
	 */
	public void validateAuthFilters(AuthRequestDTO authRequestDto,
			           Map<String, List<IdentityInfoDTO>> identityData,
			           Map<String, Object> properties,
			           boolean isExternalAuth) throws IdAuthenticationFilterException {
		MosipAuthFilterFactory mosipAuthFilterFactory = isExternalAuth ? externalAuthFilterFactory : internalAuthFilterFactory;
		List<IMosipAuthFilter> enabledAuthFilters = mosipAuthFilterFactory.getEnabledAuthFilters();
		for (IMosipAuthFilter authFilter : enabledAuthFilters) {
			// This will run auth filter validate one by one and any exception thrown from
			// one filter will skip the execution of the rest.
			authFilter.validate(authRequestDto, identityData, properties);
		}
	}

}

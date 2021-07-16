package io.mosip.authentication.childauthfilter.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.kernel.core.util.DateUtils;

/**
 * The Class ChildAuthFilterImpl - implementation of auth filter for
 * validating AuthType locked/unlocked status for an individual in the
 * authentication request.
 * 
 * @author Loganathan Sekar
 */
public class ChildAuthFilterImpl implements IMosipAuthFilter {
	
	
	private static final String OTP = "otp";

	private static final String DEMO = "demo";

	private static final String BIO = "bio";

	private static final int DEFAULT_CHILD_MAX_AGE = 6;

	@Value("${ida.child-auth-filter.date-of-birth.attribute.name:dateOfBirth}")
	private String dateOfBirthAttributeName;
	
	@Value("${ida.child-auth-filter.date-of-birth.pattern:" + IdAuthCommonConstants.DOB_PATTERN + "}")
	private String dateOfBirthPattern;
	
	@Value("${ida.child-auth-filter.child.max.age:" + DEFAULT_CHILD_MAX_AGE + "}")
	private int childMaxAge;
	
	@Value("${ida.child-auth-filter.factors.denied:otp,bio}")
	private String[] factorsDeniedForChild;

	/**
	 * Inits the.
	 */
	public void init() throws IdAuthenticationFilterException {
	}

	/**
	 * Test method that executes predicate test condition on the given arguments
	 *
	 * @param authRequest  the auth request
	 * @param identityData the identity data
	 * @param properties   the properties
	 * @throws IdAuthenticationBusinessException 
	 */
	public void validate(AuthRequestDTO authRequest, Map<String, List<IdentityInfoDTO>> identityData,
			Map<String, Object> properties) throws IdAuthenticationFilterException {
		LocalDate dob = getDateOfBirth(identityData.get(dateOfBirthAttributeName));
		if(dob.plusYears(childMaxAge).isAfter(LocalDate.now())) {
			checkDeniedFactorsForChild(authRequest);
		}
	}

	private void checkDeniedFactorsForChild(AuthRequestDTO authRequest) throws IdAuthenticationFilterException{
		List<Object> deniedFactors = Stream.of(factorsDeniedForChild)
											.map(String::toLowerCase)
											.collect(Collectors.toList());
		if(deniedFactors.contains(OTP) && authRequest.getRequestedAuth().isOtp()) {
			throw new IdAuthenticationFilterException(
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage(), OTP));
		} 

		if(deniedFactors.contains(DEMO) && authRequest.getRequestedAuth().isDemo()) {
			throw new IdAuthenticationFilterException(
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage(), DEMO));
		} 

		if(deniedFactors.contains(BIO) && authRequest.getRequestedAuth().isBio()) {
			throw new IdAuthenticationFilterException(
					IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorMessage(), BIO));
		} 
		
	}

	private LocalDate getDateOfBirth(List<IdentityInfoDTO> dobData) throws IdAuthenticationFilterException {
		if(dobData == null || dobData.isEmpty()) {
			throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(), String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(), dateOfBirthAttributeName));
		}
		
		IdentityInfoDTO identityInfoDTO = dobData.get(0);
		String dob = identityInfoDTO.getValue();
		
		if(dob == null || dob.trim().isEmpty()) {
			throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorCode(), String.format(IdAuthenticationErrorConstants.ID_NOT_AVAILABLE.getErrorMessage(), dateOfBirthAttributeName));
		}
		
		try {
			return DateUtils.parseDateToLocalDateTime(DateUtils.parseToDate(dob, dateOfBirthPattern)).toLocalDate();
		} catch (Exception e) {
			throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), "Request could not be processed. Unable to parse " + dateOfBirthAttributeName + " from DB.");
		}
	}
	
}

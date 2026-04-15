package io.mosip.authentication.childauthfilter.impl;

import io.mosip.authentication.authfilter.exception.IdAuthenticationFilterException;
import io.mosip.authentication.authfilter.spi.IMosipAuthFilter;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.kernel.core.util.DateUtils2;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Class ChildAuthFilterImpl - implementation of auth filter for
 * validating AuthType locked/unlocked status for an individual in the
 * authentication request.
 *
 * @author Kamesh Shekhar Prasad
 */
public class ChildAuthFilterImpl implements IMosipAuthFilter {


    private static final String ERROR_MSG_UNSUPPORTED_AUTH_TYPE = "Unsupported Authentication Type for child - %s";

    /** The Constant OTP. */
    private static final String OTP = "otp";

    /** The Constant DEMO. */
    private static final String DEMO = "demo";

    /** The Constant BIO. */
    private static final String BIO = "bio";

    /** The Constant DEFAULT_CHILD_MAX_AGE. */
    private static final int DEFAULT_CHILD_MAX_AGE = 6;

    /** The date of birth attribute name. */
    @Value("${ida.child-auth-filter.date-of-birth.attribute.name:dateOfBirth}")
    private String dateOfBirthAttributeName;

    /** The date of birth pattern. */
    @Value("${ida.child-auth-filter.date-of-birth.pattern:" + IdAuthCommonConstants.DEFAULT_DOB_PATTERN + "}")
    private String dateOfBirthPattern;

    /** The child max age. */
    @Value("${ida.child-auth-filter.child.max.age:" + DEFAULT_CHILD_MAX_AGE + "}")
    private int childMaxAge;

    /** The factors denied for child. */
    @Value("${ida.child-auth-filter.factors.denied:otp,bio}")
    private String[] factorsDeniedForChild;

    /**
     * Initializes the filter.
     *
     * @throws IdAuthenticationFilterException the id authentication filter exception
     */
    public void init() throws IdAuthenticationFilterException {
    }

    /**
     * Test method that executes predicate test condition on the given arguments.
     *
     * @param authRequest  the auth request
     * @param identityData the identity data
     * @param properties   the properties
     * @throws IdAuthenticationFilterException the id authentication filter exception
     */
    public void validate(AuthRequestDTO authRequest, Map<String, List<IdentityInfoDTO>> identityData,
                         Map<String, Object> properties) throws IdAuthenticationFilterException {
        System.out.println("[ChildAuthFilter] ===== ChildAuthFilter validate() called =====");
        System.out.println("[ChildAuthFilter] dateOfBirthAttributeName: " + dateOfBirthAttributeName);
        System.out.println("[ChildAuthFilter] dateOfBirthPattern: " + dateOfBirthPattern);
        System.out.println("[ChildAuthFilter] childMaxAge: " + childMaxAge);
        System.out.println("[ChildAuthFilter] factorsDeniedForChild: " + java.util.Arrays.toString(factorsDeniedForChild));
        System.out.println("[ChildAuthFilter] identityData keys: " + identityData.keySet());
        System.out.println("[ChildAuthFilter] raw DOB data from identity: " + identityData.get(dateOfBirthAttributeName));
        System.out.println("[ChildAuthFilter] isOtp: " + AuthTypeUtil.isOtp(authRequest));
        System.out.println("[ChildAuthFilter] isDemo: " + AuthTypeUtil.isDemo(authRequest));
        System.out.println("[ChildAuthFilter] isBio: " + AuthTypeUtil.isBio(authRequest));

        LocalDate dob = getDateOfBirth(identityData.get(dateOfBirthAttributeName));
        LocalDate today = LocalDate.now();
        LocalDate childCutoff = dob.plusYears(childMaxAge);
        System.out.println("[ChildAuthFilter] parsed DOB: " + dob);
        System.out.println("[ChildAuthFilter] today: " + today);
        System.out.println("[ChildAuthFilter] childCutoff (dob + childMaxAge): " + childCutoff);
        System.out.println("[ChildAuthFilter] isChild (childCutoff.isAfter(today)): " + childCutoff.isAfter(today));

        if(childCutoff.isAfter(today)) {
            System.out.println("[ChildAuthFilter] Individual is identified as a CHILD — checking denied factors");
            checkDeniedFactorsForChild(authRequest);
        } else {
            System.out.println("[ChildAuthFilter] Individual is NOT a child — skipping denied factor check");
        }
    }

    /**
     * Check denied factors for child.
     *
     * @param authRequest the auth request
     * @throws IdAuthenticationFilterException the id authentication filter exception
     */
    private void checkDeniedFactorsForChild(AuthRequestDTO authRequest) throws IdAuthenticationFilterException{
        List<Object> deniedFactors = Stream.of(factorsDeniedForChild)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        System.out.println("[ChildAuthFilter] deniedFactors list: " + deniedFactors);

        if(deniedFactors.contains(OTP) && AuthTypeUtil.isOtp(authRequest)) {
            System.out.println("[ChildAuthFilter] BLOCKING: OTP auth denied for child");
            throw new IdAuthenticationFilterException(
                    IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
                    String.format(ERROR_MSG_UNSUPPORTED_AUTH_TYPE, OTP));
        }

        if(deniedFactors.contains(DEMO) && AuthTypeUtil.isDemo(authRequest)) {
            System.out.println("[ChildAuthFilter] BLOCKING: DEMO auth denied for child");
            throw new IdAuthenticationFilterException(
                    IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
                    String.format(ERROR_MSG_UNSUPPORTED_AUTH_TYPE, DEMO));
        }

        if(deniedFactors.contains(BIO) && AuthTypeUtil.isBio(authRequest)) {
            System.out.println("[ChildAuthFilter] BLOCKING: BIO auth denied for child");
            throw new IdAuthenticationFilterException(
                    IdAuthenticationErrorConstants.AUTH_TYPE_NOT_SUPPORTED.getErrorCode(),
                    String.format(ERROR_MSG_UNSUPPORTED_AUTH_TYPE, BIO));
        }

    }

    /**
     * Gets the date of birth.
     *
     * @param dobData the dob data
     * @return the date of birth
     * @throws IdAuthenticationFilterException the id authentication filter exception
     */
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
            return DateUtils2.parseDateToLocalDateTime(DateUtils2.parseToDate(dob, dateOfBirthPattern)).toLocalDate();
        } catch (Exception e) {
            throw new IdAuthenticationFilterException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorCode(), "Request could not be processed. Unable to parse " + dateOfBirthAttributeName + " from DB.");
        }
    }

}

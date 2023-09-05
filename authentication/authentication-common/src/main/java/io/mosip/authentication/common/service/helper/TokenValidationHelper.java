package io.mosip.authentication.common.service.helper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.entity.OIDCClientData;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.common.service.repository.OIDCClientDataRepository;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.KycTokenStatusType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Helper class to Validate Token returned in kyc-auth.
 *
 * @author Mahammed Taheer
 */

public class TokenValidationHelper {
    
    	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(TokenValidationHelper.class);

    @Value("${ida.idp.consented.individual_id.attribute.name:individual_id}")
	private String consentedIndividualIdAttributeName;


    /** The Kyc Service */
	@Autowired
	private KycService kycService;

    @Autowired
	private KycTokenDataRepository kycTokenDataRepo;

	@Autowired
	private IdInfoHelper idInfoHelper;

    @Autowired
	private OIDCClientDataRepository oidcClientDataRepo; 


    public KycTokenData findAndValidateIssuedToken(String tokenData, String oidcClientId, String reqTransactionId, 
        String idvidHash) throws IdAuthenticationBusinessException {

        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "processVciExchange",
						"Check Token Exists or not, associated with oidc client and active status.");
						
        Optional<KycTokenData> tokenDataOpt = kycTokenDataRepo.findByKycToken(tokenData);
        if (!tokenDataOpt.isPresent()) {
            mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
                    "KYC Token not found: " + tokenData);
            throw new IdAuthenticationBusinessException(
                        IdAuthenticationErrorConstants.KYC_TOKEN_NOT_FOUND.getErrorCode(),
                        IdAuthenticationErrorConstants.KYC_TOKEN_NOT_FOUND.getErrorMessage());
        }
        KycTokenData tokenDataObj = tokenDataOpt.get();
        validateToken(tokenDataObj, oidcClientId, reqTransactionId, idvidHash);
        return tokenDataObj;
    }

    private void validateToken(KycTokenData kycTokenData, String oidcClientId, String reqTransactionId, String idvidHash) 
				throws IdAuthenticationBusinessException {
		String kycToken = kycTokenData.getKycToken();
		if (kycTokenData.getKycTokenStatus().equals(KycTokenStatusType.PROCESSED.getStatus())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateKycToken",
					"KYC Token already processed: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_ALREADY_PROCESSED.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_ALREADY_PROCESSED.getErrorMessage());
		}

		if (kycTokenData.getKycTokenStatus().equals(KycTokenStatusType.EXPIRED.getStatus())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateKycToken",
					"KYC Token expired: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorMessage());
		}

		if (!kycTokenData.getOidcClientId().equals(oidcClientId)) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateKycToken",
					"KYC Token does not belongs to the provided OIDC Client Id: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_OIDC_CLIENT_ID.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_OIDC_CLIENT_ID.getErrorMessage());
		}

		if (!kycTokenData.getIdVidHash().equals(idvidHash)) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateKycToken",
					"KYC Token does not belongs to the provided UIN/VID: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_UIN_VID.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_UIN_VID.getErrorMessage());
		}

		if (!kycTokenData.getRequestTransactionId().equals(reqTransactionId)) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateKycToken",
					"KYC Auth & KYC Exchange Transaction Ids are not same: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_TRANSACTION_ID.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_TRANSACTION_ID.getErrorMessage());
		}

		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateKycToken",
					"KYC Token found, Check Token expire.");
		LocalDateTime tokenIssuedDateTime = kycTokenData.getTokenIssuedDateTime();
		boolean isExpired = kycService.isKycTokenExpire(tokenIssuedDateTime, kycToken);

		if (isExpired) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "validateKycToken", 
					"KYC Token expired.");
			kycTokenData.setKycTokenStatus(KycTokenStatusType.EXPIRED.getStatus());
			kycTokenDataRepo.saveAndFlush(kycTokenData);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorMessage());
		}
	}

	public void mapConsentedAttributesToIdSchemaAttributes(List<String> consentAttributes, Set<String> filterAttributes, 
			List<String> policyAllowedKycAttribs) throws IdAuthenticationBusinessException {

		if(consentAttributes != null && !consentAttributes.isEmpty()) {
			for (String attrib : consentAttributes) {
				Collection<? extends String> idSchemaAttribute = idInfoHelper.getIdentityAttributesForIdName(attrib);
				filterAttributes.addAll(idSchemaAttribute);
			}
			// removing individual id from consent if the claim is not allowed in policy.
			if (!policyAllowedKycAttribs.contains(consentedIndividualIdAttributeName)) {
				consentAttributes.remove(consentedIndividualIdAttributeName);
			}
		}
	} 

	public Set<String> filterByPolicyAllowedAttributes(Set<String> filterAttributes, List<String> policyAllowedKycAttribs) {
		return policyAllowedKycAttribs.stream()
							.filter(attribute -> filterAttributes.contains(attribute))
							.collect(Collectors.toSet());
	}

	public String getKycExchangeResponseTime(BaseRequestDTO authRequestDTO) {
		String dateTimePattern = EnvUtil.getDateTimePattern();
		return IdaRequestResponsConsumerUtil.getResponseTime(authRequestDTO.getRequestTime(), dateTimePattern);
	}

	public List<String> filterAllowedUserClaims(String oidcClientId, List<String> consentAttributes) {
		mosipLogger.info(IdAuthCommonConstants.IDA, this.getClass().getSimpleName(), "filterAllowedUserClaims", 
					"Checking for OIDC client allowed userclaims");
		Optional<OIDCClientData> oidcClientData = oidcClientDataRepo.findByClientId(oidcClientId);

		List<String> oidcClientAllowedUserClaims = List.of(oidcClientData.get().getUserClaims())
													   .stream()
													   .map(String::toLowerCase)
													   .collect(Collectors.toList());
		if (consentAttributes.isEmpty()) {
			return oidcClientAllowedUserClaims;
		}

		return consentAttributes.stream()
							    .filter(claim -> oidcClientAllowedUserClaims.contains(claim.toLowerCase()))
								.collect(Collectors.toList());

	}
}

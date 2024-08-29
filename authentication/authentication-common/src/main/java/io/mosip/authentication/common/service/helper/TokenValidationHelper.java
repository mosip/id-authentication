package io.mosip.authentication.common.service.helper;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.KycTokenStatusType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * Helper class to Validate Token returned in kyc-auth.
 *
 * @author Mahammed Taheer
 */

@Component
public class TokenValidationHelper {
    
    	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(TokenValidationHelper.class);

    /** The Kyc Service */
	@Autowired
	private KycService kycService;

    @Autowired
	private KycTokenDataRepository kycTokenDataRepo;

    public KycTokenData findAndValidateIssuedToken(String tokenData, String oidcClientId, String reqTransactionId, 
        String idvidHash) throws IdAuthenticationBusinessException {

        mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
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
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
					"KYC Token already processed: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_ALREADY_PROCESSED.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_ALREADY_PROCESSED.getErrorMessage());
		}

		if (kycTokenData.getKycTokenStatus().equals(KycTokenStatusType.EXPIRED.getStatus())) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
					"KYC Token expired: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorMessage());
		}

		if (!kycTokenData.getOidcClientId().equals(oidcClientId)) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
					"KYC Token does not belongs to the provided OIDC Client Id: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_OIDC_CLIENT_ID.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_OIDC_CLIENT_ID.getErrorMessage());
		}

		if (!kycTokenData.getIdVidHash().equals(idvidHash)) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
					"KYC Token does not belongs to the provided UIN/VID: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_UIN_VID.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_UIN_VID.getErrorMessage());
		}

		if (!kycTokenData.getRequestTransactionId().equals(reqTransactionId)) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
					"KYC Auth & KYC Exchange Transaction Ids are not same: " + kycToken);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_TRANSACTION_ID.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_INVALID_TRANSACTION_ID.getErrorMessage());
		}

		mosipLogger.info(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken",
					"KYC Token found, Check Token expire.");
		LocalDateTime tokenIssuedDateTime = kycTokenData.getTokenIssuedDateTime();
		boolean isExpired = kycService.isKycTokenExpire(tokenIssuedDateTime, kycToken);

		if (isExpired) {
			mosipLogger.error(IdAuthCommonConstants.SESSION_ID, this.getClass().getSimpleName(), "findAndValidateIssuedToken", 
					"KYC Token expired.");
			kycTokenData.setKycTokenStatus(KycTokenStatusType.EXPIRED.getStatus());
			kycTokenDataRepo.saveAndFlush(kycTokenData);
			throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorCode(),
						IdAuthenticationErrorConstants.KYC_TOKEN_EXPIRED.getErrorMessage());
		}
	}
}

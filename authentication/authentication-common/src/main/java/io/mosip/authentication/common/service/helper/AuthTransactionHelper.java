package io.mosip.authentication.common.service.helper;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthTypeDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;

/**
 * The Class AuthTransactionHelper.
 */
@Component
public class AuthTransactionHelper {
	
	/** The uin encrypt salt repo. */
	@Autowired
	private UinEncryptSaltRepo uinEncryptSaltRepo;

	/** The uin hash salt repo. */
	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;
	
	/** The env. */
	@Autowired
	private Environment env;
	
	/** The security manager. */
	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Builds the auth transaction entity.
	 *
	 * @param authTxnBuilder the auth txn builder
	 * @return the autn txn
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public AutnTxn buildAuthTransactionEntity(AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		return authTxnBuilder.build(env, uinEncryptSaltRepo, uinHashSaltRepo, securityManager);
	}
	
	public void setAuthTransactionBuilderMetadata(ObjectWithMetadata objectWithMetadata, AuthTransactionBuilder authTxnBuilder) {
		setObjectToMetadata(objectWithMetadata, getAuthTransactionBuilderKey(), authTxnBuilder);
	}
	
	public void setAuthTransactionEntityMetadata(IdAuthenticationBaseException exception , AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		try {
			authTxnBuilder.withStatusComment(objectMapper.writeValueAsString(IdAuthExceptionHandler.getAuthErrors(exception)));
		} catch (JsonProcessingException e) {
			authTxnBuilder.withStatusComment(e.getMessage() == null ? IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage() : e.getMessage());
		}
		
		if(authTxnBuilder.getToken() == null) {
			authTxnBuilder.withToken(IdAuthCommonConstants.UNKNOWN);
		}
		
		if(authTxnBuilder.getRequestTypes() == null || authTxnBuilder.getRequestTypes().isEmpty()) {
			authTxnBuilder.withAuthTypeCode(IdAuthCommonConstants.UNKNOWN);
		}
		
		setObjectToMetadata(exception, getAuthTransactionEntityKey(), buildAuthTransactionEntity(authTxnBuilder));
	}
	
	public void setAuthTransactionEntityMetadata(ObjectWithMetadata objectWithMetadata , AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		setObjectToMetadata(objectWithMetadata, getAuthTransactionEntityKey(), buildAuthTransactionEntity(authTxnBuilder));
	}
	
	public void setObjectToMetadata(ObjectWithMetadata objectWithMetadata, String key, Object value) {
		objectWithMetadata.putMetadata(key, value);
	}

	private String getAuthTransactionBuilderKey() {
		return AuthTransactionBuilder.class.getSimpleName();
	}
	
	private String getAuthTransactionEntityKey() {
		return AutnTxn.class.getSimpleName();
	}

	public AuthTransactionBuilder createAndSetAuthTxnBuilderMetadataToRequest(ObjectWithMetadata requestDTO, boolean isInternal, Optional<PartnerDTO> partner)
			throws IdAuthenticationBusinessException {
		AuthTransactionBuilder authTxnBuilder = createAuthTxnBuilder(requestDTO,
				isInternal, partner);
		setAuthTransactionBuilderMetadata(requestDTO, authTxnBuilder);
		return authTxnBuilder;
	}
	
	public IdAuthenticationAppException createDataValidationException(AuthTransactionBuilder authTxnBuilder, IDDataValidationException e)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		setAuthTransactionEntityMetadata(e, authTxnBuilder);
		return new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
	}

	public IdAuthenticationAppException createUnableToProcessException(AuthTransactionBuilder authTxnBuilder, IdAuthenticationBusinessException e)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		setAuthTransactionEntityMetadata(e, authTxnBuilder);
		return new IdAuthenticationAppException( IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
	}
	
	private AuthTransactionBuilder createAuthTxnBuilder(ObjectWithMetadata requestDTO,
			boolean isInternal, Optional<PartnerDTO> partner) throws IdAuthenticationBusinessException {
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance()
						.withInternal(isInternal)
						.withPartner(partner);
		
		if(requestDTO instanceof AuthRequestDTO) {
			AuthRequestDTO authRequestDTO = (AuthRequestDTO) requestDTO;
			authTransactionBuilder.withAuthRequest(authRequestDTO);
			addAuthTypes(requestDTO, authTransactionBuilder, authRequestDTO);

		} else if(requestDTO instanceof OtpRequestDTO) {
			OtpRequestDTO otpRequestDTO = (OtpRequestDTO) requestDTO;
			authTransactionBuilder.withOtpRequest(otpRequestDTO);
			authTransactionBuilder.addRequestType(RequestType.OTP_REQUEST);
		}
		
		return authTransactionBuilder;
	}

	private void addAuthTypes(ObjectWithMetadata requestDTO, AuthTransactionBuilder authTransactionBuilder,
			AuthRequestDTO authRequestDTO) {
		AuthTypeDTO requestedAuth = authRequestDTO.getRequestedAuth();
		if(requestedAuth != null) {
			if(requestedAuth.isOtp()) {
				authTransactionBuilder.addRequestType(RequestType.OTP_AUTH);
			}
			if(requestedAuth.isDemo()) {
				authTransactionBuilder.addRequestType(RequestType.DEMO_AUTH);
			}
			if(requestedAuth.isBio()) {
				if (AuthTransactionHelper.isFingerAuth(authRequestDTO, env)) {
					authTransactionBuilder.addRequestType(RequestType.FINGER_AUTH);
				}
				if (AuthTransactionHelper.isIrisAuth(authRequestDTO, env)) {
					authTransactionBuilder.addRequestType(RequestType.IRIS_AUTH);
				}
				if (AuthTransactionHelper.isFaceAuth(authRequestDTO, env)) {
					authTransactionBuilder.addRequestType(RequestType.FACE_AUTH);
				}
			}
		}
		if(requestDTO instanceof KycAuthRequestDTO) {
			authTransactionBuilder.addRequestType(RequestType.KYC_AUTH_REQUEST);
		}
	}

	public static boolean isFingerAuth(AuthRequestDTO authRequestDTO, Environment env) {
		return authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData).anyMatch(
				bioInfo -> bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_IMG.getType()) || (FMR_ENABLED_TEST.test(env)
						&& bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_MIN.getType())));
	}
	
	public static boolean isIrisAuth(AuthRequestDTO authRequestDTO, Environment env) {
		return authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(BioAuthType.IRIS_IMG.getType()));
	}
	
	public static boolean isFaceAuth(AuthRequestDTO authRequestDTO, Environment env) {
		return authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FACE_IMG.getType()));
	}
}

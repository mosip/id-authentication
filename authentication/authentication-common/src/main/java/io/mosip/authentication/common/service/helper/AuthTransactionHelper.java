package io.mosip.authentication.common.service.helper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.manager.IdAuthFraudAnalysisEventManager;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.exception.IdAuthExceptionHandler;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.repository.IdaUinHashSaltRepo;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.mosip.authentication.core.exception.IdAuthenticationBaseException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.BaseRequestDTO;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityKeyBindingRequestDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import io.mosip.authentication.core.indauth.dto.VciExchangeRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.otp.dto.OtpRequestDTO;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class AuthTransactionHelper - the helper to create auth transaction entity
 * 
 * @author Loganathan Sekar
 */
@Component
public class AuthTransactionHelper {
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthTransactionHelper.class);
	
	/** The uin hash salt repo. */
	@Autowired
	private IdaUinHashSaltRepo uinHashSaltRepo;
	
	/** The env. */
	@Autowired
	private EnvUtil env;
	
	/** The security manager. */
	@Autowired
	private IdAuthSecurityManager securityManager;
	
	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;
	
	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idService;
	
	@Autowired
	private IdAuthFraudAnalysisEventManager fraudEventManager;

	/**
	 * Builds the auth transaction entity.
	 *
	 * @param authTxnBuilder the auth txn builder
	 * @return the autn txn
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public AutnTxn buildAuthTransactionEntity(AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		AutnTxn authTxn = authTxnBuilder.build(env, uinHashSaltRepo, securityManager);
		fraudEventManager.analyseEvent(authTxn);
		return authTxn;
	}
	
	/**
	 * Sets the auth transaction builder metadata.
	 *
	 * @param objectWithMetadata the object with metadata
	 * @param authTxnBuilder the auth txn builder
	 */
	public void setAuthTransactionBuilderMetadata(ObjectWithMetadata objectWithMetadata, AuthTransactionBuilder authTxnBuilder) {
		setObjectToMetadata(objectWithMetadata, getAuthTransactionBuilderKey(), authTxnBuilder);
	}
	
	/**
	 * Sets the auth transaction entity metadata.
	 *
	 * @param exception the exception
	 * @param authTxnBuilder the auth txn builder
	 * @param targetObjectMetadata 
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void setAuthTransactionEntityMetadata(IdAuthenticationBaseException exception , AuthTransactionBuilder authTxnBuilder, ObjectWithMetadata targetObjectMetadata) throws IdAuthenticationBusinessException {
		try {
			List<AuthError> authErrors = IdAuthExceptionHandler.getAuthErrors(exception);
			targetObjectMetadata.putMetadata(IdAuthCommonConstants.ERRORS, authErrors);
			authTxnBuilder.withStatusComment(objectMapper.writeValueAsString(authErrors));
		} catch (JsonProcessingException e) {
			authTxnBuilder.withStatusComment(e.getMessage() == null ? IdAuthenticationErrorConstants.UNABLE_TO_PROCESS.getErrorMessage() : e.getMessage());
		}
		
		if(authTxnBuilder.getToken() == null) {
			try {
				authTxnBuilder.withToken(computeToken(authTxnBuilder));
			} catch (RuntimeException | IdAuthenticationBusinessException e) {
				logger.error("Error in getting token id for auth transaction. Skipping to set token id in auth transaction entry: {}", ExceptionUtils.getStackTrace(e));
				// Ignoring the error, otherwiser it will override the actual exception.
			}
		}
		
		if(authTxnBuilder.getRequestTypes() == null || authTxnBuilder.getRequestTypes().isEmpty()) {
			authTxnBuilder.withAuthTypeCode(IdAuthCommonConstants.UNKNOWN);
		}
		
		setObjectToMetadata(targetObjectMetadata, getAuthTransactionEntityKey(), buildAuthTransactionEntity(authTxnBuilder));
	}

	private String computeToken(AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		BaseRequestDTO requestDTO = authTxnBuilder.getRequestDTO();
		if(requestDTO != null) {
			String idvid = requestDTO.getIndividualId();
			String idvIdType = IdType.getIDTypeStrOrDefault(requestDTO.getIndividualIdType());
			logger.debug(IdAuthCommonConstants.SESSION_ID, "AuthTransactionHelper", "computeToken");

			Map<String, Object> idResDTO = idService.processIdType(idvIdType, idvid,
					false, false, Collections.emptySet());
			
			String token = idService.getToken(idResDTO);
			return token;
		}
		return IdAuthCommonConstants.UNKNOWN;
	}
	
	/**
	 * Sets the auth transaction entity metadata.
	 *
	 * @param objectWithMetadata the object with metadata
	 * @param authTxnBuilder the auth txn builder
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public void setAuthTransactionEntityMetadata(ObjectWithMetadata objectWithMetadata , AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		setObjectToMetadata(objectWithMetadata, getAuthTransactionEntityKey(), buildAuthTransactionEntity(authTxnBuilder));
	}
	
	/**
	 * Sets the object to metadata.
	 *
	 * @param objectWithMetadata the object with metadata
	 * @param key the key
	 * @param value the value
	 */
	public void setObjectToMetadata(ObjectWithMetadata objectWithMetadata, String key, Object value) {
		objectWithMetadata.putMetadata(key, value);
	}

	/**
	 * Gets the auth transaction builder key.
	 *
	 * @return the auth transaction builder key
	 */
	private String getAuthTransactionBuilderKey() {
		return AuthTransactionBuilder.class.getSimpleName();
	}
	
	/**
	 * Gets the auth transaction entity key.
	 *
	 * @return the auth transaction entity key
	 */
	private String getAuthTransactionEntityKey() {
		return AutnTxn.class.getSimpleName();
	}

	/**
	 * Creates the and set auth txn builder metadata to request.
	 *
	 * @param requestDTO the request DTO
	 * @param isInternal the is internal
	 * @param partner the partner
	 * @return the auth transaction builder
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public AuthTransactionBuilder createAndSetAuthTxnBuilderMetadataToRequest(ObjectWithMetadata requestDTO, boolean isInternal, 
				Optional<PartnerDTO> partner) throws IdAuthenticationBusinessException {
		AuthTransactionBuilder authTxnBuilder = createAuthTxnBuilder(requestDTO, isInternal, partner);
		setAuthTransactionBuilderMetadata(requestDTO, authTxnBuilder);
		return authTxnBuilder;
	}
	
	/**
	 * Creates the data validation exception.
	 *
	 * @param authTxnBuilder the auth txn builder
	 * @param e the e
	 * @param targetObjectMetadata 
	 * @return the id authentication app exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public IdAuthenticationAppException createDataValidationException(AuthTransactionBuilder authTxnBuilder, IDDataValidationException e, ObjectWithMetadata targetObjectMetadata)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		setAuthTransactionEntityMetadata(e, authTxnBuilder, targetObjectMetadata);
		return new IdAuthenticationAppException(IdAuthenticationErrorConstants.DATA_VALIDATION_FAILED, e);
	}

	/**
	 * Creates the unable to process exception.
	 *
	 * @param authTxnBuilder the auth txn builder
	 * @param e the e
	 * @param requestWithMetadata 
	 * @return the id authentication app exception
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 * @throws IdAuthenticationAppException the id authentication app exception
	 */
	public IdAuthenticationAppException createUnableToProcessException(AuthTransactionBuilder authTxnBuilder, IdAuthenticationBusinessException e, ObjectWithMetadata requestWithMetadata)
			throws IdAuthenticationBusinessException, IdAuthenticationAppException {
		setAuthTransactionEntityMetadata(e, authTxnBuilder, requestWithMetadata);
		return new IdAuthenticationAppException( IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
	}
	
	/**
	 * Creates the auth txn builder.
	 *
	 * @param requestDTO the request DTO
	 * @param isInternal the is internal
	 * @param partner the partner
	 * @return the auth transaction builder
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private AuthTransactionBuilder createAuthTxnBuilder(ObjectWithMetadata requestDTO,
			boolean isInternal, Optional<PartnerDTO> partner) throws IdAuthenticationBusinessException {
		AuthTransactionBuilder authTransactionBuilder = AuthTransactionBuilder.newInstance()
						.withInternal(isInternal)
						.withPartner(partner);
		
		if(requestDTO instanceof AuthRequestDTO) {
			AuthRequestDTO authRequestDTO = (AuthRequestDTO) requestDTO;
			authTransactionBuilder.withRequest(authRequestDTO);
			addAuthTypes(requestDTO, authTransactionBuilder, authRequestDTO);

		} else if(requestDTO instanceof OtpRequestDTO) {
			OtpRequestDTO otpRequestDTO = (OtpRequestDTO) requestDTO;
			authTransactionBuilder.withRequest(otpRequestDTO);
			authTransactionBuilder.addRequestType(RequestType.OTP_REQUEST);
		} else if(requestDTO instanceof KycExchangeRequestDTO) {
			KycExchangeRequestDTO kycExcRequestDTO = (KycExchangeRequestDTO) requestDTO;
			authTransactionBuilder.withRequest(kycExcRequestDTO);
			authTransactionBuilder.addRequestType(RequestType.KYC_EXCHANGE_REQUEST);
		} else if(requestDTO instanceof IdentityKeyBindingRequestDTO) {
			IdentityKeyBindingRequestDTO keyBindingRequestDTO = (IdentityKeyBindingRequestDTO) requestDTO;
			authTransactionBuilder.withRequest(keyBindingRequestDTO);
			authTransactionBuilder.addRequestType(RequestType.IDENTITY_KEY_BINDING);
		} else if(requestDTO instanceof VciExchangeRequestDTO) {
			VciExchangeRequestDTO vciExchangeRequestDTO = (VciExchangeRequestDTO) requestDTO;
			authTransactionBuilder.withRequest(vciExchangeRequestDTO);
			authTransactionBuilder.addRequestType(RequestType.VCI_EXCHANGE_REQUEST);
		}

		
		return authTransactionBuilder;
	}

	/**
	 * Adds the auth types.
	 *
	 * @param requestDTO the request DTO
	 * @param authTransactionBuilder the auth transaction builder
	 * @param authRequestDTO the auth request DTO
	 */
	private void addAuthTypes(ObjectWithMetadata requestDTO, AuthTransactionBuilder authTransactionBuilder,
			AuthRequestDTO authRequestDTO) {
		List<RequestType> findAutRequestTypes = AuthTypeUtil.findAutRequestTypes(authRequestDTO, env);
		findAutRequestTypes.forEach(authTransactionBuilder::addRequestType);
	}

	/**
	 * Checks if is finger auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param env the env
	 * @return true, if is finger auth
	 */
	public static boolean isFingerAuth(AuthRequestDTO authRequestDTO, EnvUtil env) {
		return authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData).anyMatch(
				bioInfo -> bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_IMG.getType()) || EnvUtil.getIsFmrEnabled()
						&& bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_MIN.getType()));
	}
	
	/**
	 * Checks if is iris auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param env the env
	 * @return true, if is iris auth
	 */
	public static boolean isIrisAuth(AuthRequestDTO authRequestDTO, EnvUtil env) {
		return authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(BioAuthType.IRIS_IMG.getType()));
	}
	
	/**
	 * Checks if is face auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param env the env
	 * @return true, if is face auth
	 */
	public static boolean isFaceAuth(AuthRequestDTO authRequestDTO, EnvUtil env) {
		return authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FACE_IMG.getType()));
	}
}

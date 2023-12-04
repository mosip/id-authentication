/*
 * 
 */
package io.mosip.authentication.common.service.facade;

import static io.mosip.authentication.core.constant.AuthTokenType.PARTNER;
import static io.mosip.authentication.core.constant.AuthTokenType.POLICY;
import static io.mosip.authentication.core.constant.AuthTokenType.POLICY_GROUP;
import static io.mosip.authentication.core.constant.AuthTokenType.RANDOM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import io.mosip.authentication.core.spi.indauth.service.KeyBindedTokenAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthResponseBuilder;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.IdaIdMapping;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.AuthTypeUtil;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.common.service.util.IdaRequestResponsConsumerUtil;
import io.mosip.authentication.common.service.validator.AuthFiltersValidator;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.ObjectWithMetadata;
import io.mosip.authentication.core.exception.IdAuthUncheckedException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycAuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.EkycAuthRequestDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.bioauth.CbeffDocType;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.indauth.service.PasswordAuthService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.spi.partner.service.PartnerService;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * This class provides the implementation of AuthFacade, provides the
 * authentication for individual by calling the respective Service
 * Classes{@link AuthFacade}.
 *
 * @author Arun Bose
 * 
 * @author Prem Kumar
 */
@Service
public class AuthFacadeImpl implements AuthFacade {

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthFacadeImpl.class);

	/** The otp service. */
	@Autowired
	private OTPAuthService otpAuthService;

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idService;

	/** The id auth service. */
	@Autowired
	private AuditHelper auditHelper;

	/** The EnvPropertyResolver */
	@Autowired
	private EnvUtil env;

	/** The Demo Auth Service */
	@Autowired
	private DemoAuthService demoAuthService;

	/** The BioAuthService */
	@Autowired
	private BioAuthService bioAuthService;

	/** The NotificationService */
	@Autowired
	private NotificationService notificationService;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private AuthTransactionHelper authTransactionHelper;

	@Autowired
	private AuthFiltersValidator authFiltersValidator;
	
	@Autowired
	private IdInfoHelper idInfoHelper;

	@Autowired
	private KeyBindedTokenAuthService keyBindedTokenAuthService;

	@Autowired
	private PasswordAuthService passwordAuthService;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.AuthFacade#
	 * authenticateApplicant(io.mosip.authentication.core.dto.indauth.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequestDTO, boolean isExternalAuth, String partnerId,
			String partnerApiKey, boolean markVidConsumed, ObjectWithMetadata requestWrapperMetadata) throws IdAuthenticationBusinessException {

		String idvid = authRequestDTO.getIndividualId();
		String idvidHash = securityManager.hash(idvid);
		String idvIdType = IdType.getIDTypeStrOrDefault(authRequestDTO.getIndividualIdType());
		logger.debug(IdAuthCommonConstants.SESSION_ID, "AuthFacedImpl", "authenticateIndividual: ",
				idvIdType + "-" + idvidHash);

		Set<String> filterAttributes = new HashSet<>();
		filterAttributes.addAll(idInfoHelper.buildDemoAttributeFilters(authRequestDTO));
		filterAttributes.addAll(idInfoHelper.buildBioFilters(authRequestDTO));
		
		if(authRequestDTO instanceof EkycAuthRequestDTO) {
			EkycAuthRequestDTO kycAuthRequestDTO = (EkycAuthRequestDTO) authRequestDTO;
			// In case of ekyc request and photo also needed we need to add face to get it
			// filtered
			if(IdInfoHelper.isKycAttributeHasPhoto(kycAuthRequestDTO)) {
				filterAttributes.add(CbeffDocType.FACE.getType().value());
			}
			
			addKycPolicyAttributes(filterAttributes, kycAuthRequestDTO);
		}

		if(authRequestDTO instanceof KycAuthRequestDTO) {
			KycAuthRequestDTO kycAuthRequestDTO = (KycAuthRequestDTO) authRequestDTO;
			// In case of kyc-auth request and password auth is requested
			if(AuthTypeUtil.isPassword(kycAuthRequestDTO)) {
				filterAttributes.add(IdaIdMapping.PASSWORD.getIdname());
			}
		}
		
		Map<String, Object> idResDTO = idService.processIdType(idvIdType, idvid, idInfoHelper.isBiometricDataNeeded(authRequestDTO),
				markVidConsumed, filterAttributes);

		String token = idService.getToken(idResDTO);

		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance();
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String authTokenId = null;
		Boolean authTokenRequired = EnvUtil.getAuthTokenRequired();

		AuthTransactionBuilder authTxnBuilder = (AuthTransactionBuilder) authRequestDTO.getMetadata()
				.get(AuthTransactionBuilder.class.getSimpleName());
		authTxnBuilder.withToken(token);

		String transactionID = authRequestDTO.getTransactionID();
		try {
			idInfo = IdInfoFetcher.getIdInfo(idResDTO);
			authResponseBuilder.setTxnID(transactionID);
			authTokenId = authTokenRequired && isExternalAuth ? getToken(authRequestDTO, partnerId, partnerApiKey, idvid, token)
					: null;

			LinkedHashMap<String, Object> properties = new LinkedHashMap<>(authRequestDTO.getMetadata());
			properties.put(IdAuthCommonConstants.TOKEN, token);
			authFiltersValidator.validateAuthFilters(authRequestDTO, idInfo, properties);

			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, token, isExternalAuth, authTokenId,
					partnerId, authTxnBuilder, idvidHash);
			authStatusList.stream().filter(Objects::nonNull).forEach(authResponseBuilder::addAuthStatusInfo);
		} catch (IdAuthenticationBusinessException e) {
			throw e;
		} finally {
			// Set response token
			if (authTokenRequired) {
				authResponseDTO = authResponseBuilder.build(authTokenId);
			} else {
				authResponseDTO = authResponseBuilder.build(null);
			}
			
			IdaRequestResponsConsumerUtil.setIdVersionToResponse(requestWrapperMetadata, authResponseDTO);
			if(authResponseDTO.getTransactionID() == null && transactionID != null) {
				authResponseDTO.setTransactionID(transactionID);
			}

			boolean authStatus = authResponseDTO.getResponse().isAuthStatus();
			authTxnBuilder.withStatus(authStatus);
			authTxnBuilder.withAuthToken(authTokenId);

			// This is sent back for the consumption by the caller for example
			// KYCFacadeImpl, Base IDA Filter.
			requestWrapperMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, idResDTO);
			requestWrapperMetadata.putMetadata(IdAuthCommonConstants.IDENTITY_INFO, idInfo);
			requestWrapperMetadata.putMetadata(IdAuthCommonConstants.STATUS, authStatus);
			requestWrapperMetadata.putMetadata(IdAuthCommonConstants.ERRORS, authResponseDTO.getErrors());

			authTransactionHelper.setAuthTransactionEntityMetadata(requestWrapperMetadata, authTxnBuilder);

			logger.info(IdAuthCommonConstants.SESSION_ID, EnvUtil.getAppId(),
					AUTH_FACADE, "authenticateApplicant status : " + authResponseDTO.getResponse().isAuthStatus());
		}

		if (idInfo != null && idvid != null) {
			notificationService.sendAuthNotification(authRequestDTO, idvid, authResponseDTO, idInfo, isExternalAuth);
		}

		return authResponseDTO;

	}

	private void addKycPolicyAttributes(Set<String> filterAttributes, EkycAuthRequestDTO kycAuthRequestDTO)
			throws IdAuthenticationBusinessException {
		List<String> allowedKycAttributes = kycAuthRequestDTO.getAllowedKycAttributes();
		if(allowedKycAttributes != null && !allowedKycAttributes.isEmpty()) {
			for (String attrib : allowedKycAttributes) {
				filterAttributes.addAll(getIdSchemaAttributes(attrib));
			}
		}
	}

	private Collection<? extends String> getIdSchemaAttributes(String attrib) throws IdAuthenticationBusinessException {
		return idInfoHelper.getIdentityAttributesForIdName(attrib);
	}

	private String getToken(AuthRequestDTO authRequestDTO, String partnerId, String partnerApiKey, String idvid,
			String token) throws IdAuthenticationBusinessException {
		Optional<PartnerPolicyResponseDTO> policyForPartner = partnerService.getPolicyForPartner(partnerId,
				partnerApiKey, authRequestDTO.getMetadata());
		Optional<String> authTokenTypeOpt = policyForPartner.map(PartnerPolicyResponseDTO::getPolicy)
				.map(PolicyDTO::getAuthTokenType);
		if (authTokenTypeOpt.isPresent()) {
			String authTokenType = authTokenTypeOpt.get();
			if (authTokenType.equalsIgnoreCase(RANDOM.getType())) {
				return createRandomToken(authRequestDTO.getTransactionID());
			} else if (authTokenType.equalsIgnoreCase(PARTNER.getType())) {
				return tokenIdManager.generateTokenId(token, partnerId);
			} else if (authTokenType.equalsIgnoreCase(POLICY.getType())) {
				Optional<String> policyId = policyForPartner.map(PartnerPolicyResponseDTO::getPolicyId);
				if (policyId.isPresent()) {
					return tokenIdManager.generateTokenId(token, policyId.get());
				}
			} else if (authTokenType.equalsIgnoreCase(POLICY_GROUP.getType())) {
				// TODO: update with Policy Group
			}
		}
		return createRandomToken(authRequestDTO.getTransactionID());
	}

	private String createRandomToken(String transactionId) throws IdAuthenticationBusinessException {
		return securityManager.createRandomToken(transactionId.getBytes());
	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo         list of identityInfoDto request
	 * @param uin            the uin
	 * @param isAuth         the is auth
	 * @param authTokenId    the auth token id
	 * @param partnerId      the partner id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String token, boolean isAuth, String authTokenId,
			String partnerId, AuthTransactionBuilder authTxnBuilder, String idvidHash) throws IdAuthenticationBusinessException {

		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		IdType idType = IdType.getIDTypeOrDefault(authRequestDTO.getIndividualIdType());

		processOTPAuth(authRequestDTO, token, isAuth, authStatusList, idType, authTokenId, partnerId, authTxnBuilder, idvidHash);

		if (!isMatchFailed(authStatusList)) {
			processDemoAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, authTokenId, partnerId,
					authTxnBuilder, idvidHash);
		}

		if (!isMatchFailed(authStatusList)) {
			processBioAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, authTokenId, partnerId,
					authTxnBuilder, idvidHash);
		}

		if (!isMatchFailed(authStatusList)) {
			processTokenAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, authTokenId, partnerId,
					authTxnBuilder, idvidHash);
		}

		if (!isMatchFailed(authStatusList)) {
			processPasswordAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, authTokenId, partnerId,
					authTxnBuilder, idvidHash);
		}
	
		return authStatusList;
	}

	private boolean isMatchFailed(List<AuthStatusInfo> authStatusList) {
		return authStatusList.stream().anyMatch(st -> st != null && !st.isStatus());
	}

	/**
	 * process the BioAuth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo         the id info
	 * @param uin            the uin
	 * @param isAuth
	 * @param authStatusList the auth status list
	 * @param idType         the id type
	 * @param authTokenId    the response token id
	 * @param partnerId      the partner id
	 * @param authTxnBuilder
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void processBioAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String token,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId,
			AuthTransactionBuilder authTxnBuilder, String idvidHash) throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (AuthTypeUtil.isBio(authRequestDTO)) {
			AuthStatusInfo bioValidationStatus;
			try {
				bioValidationStatus = bioAuthService.authenticate(authRequestDTO, token, idInfo, partnerId, isAuth);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;

				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				saveAndAuditBioAuthTxn(authRequestDTO, token, idType, isStatus, authTokenId, !isAuth, partnerId,
						authTxnBuilder, idvidHash);
			} finally {
				logger.info(IdAuthCommonConstants.SESSION_ID, EnvUtil.getAppId(),
						AUTH_FACADE, "BioMetric Authentication status :" + statusInfo);
			}

		}
	}

	/**
	 * Process demo auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo         the id info
	 * @param uin            the uin
	 * @param isAuth         the is auth
	 * @param authStatusList the auth status list
	 * @param idType         the id type
	 * @param authTokenId    the response token id
	 * @param partnerId      the partner id
	 * @param authTxnBuilder
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void processDemoAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String token,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId,
			AuthTransactionBuilder authTxnBuilder, String idvidHash) throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (AuthTypeUtil.isDemo(authRequestDTO)) {
			AuthStatusInfo demoValidationStatus;
			try {
				demoValidationStatus = demoAuthService.authenticate(authRequestDTO, token, idInfo, partnerId);
				authStatusList.add(demoValidationStatus);
				statusInfo = demoValidationStatus;

				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				auditHelper.audit(AuditModules.DEMO_AUTH, getAuditEvent(isAuth), authRequestDTO.getTransactionID(),
						idType, "authenticateApplicant status : " + isStatus);
			} catch (IdAuthUncheckedException e) {
				throw new IdAuthenticationBusinessException(e.getErrorCode(), e.getErrorText());
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();

				logger.info(IdAuthCommonConstants.SESSION_ID, EnvUtil.getAppId(),
						AUTH_FACADE, "Demographic Authentication status : " + isStatus);
				authTxnBuilder.addRequestType(RequestType.DEMO_AUTH);
			}

		}
	}

	/**
	 * Process OTP auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin            the uin
	 * @param isAuth         the is auth
	 * @param authStatusList the auth status list
	 * @param idType         the id type
	 * @param authTokenId    the auth token id
	 * @param partnerId      the partner id
	 * @param authTxnBuilder
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void processOTPAuth(AuthRequestDTO authRequestDTO, String token, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId,
			AuthTransactionBuilder authTxnBuilder, String idvidHash) throws IdAuthenticationBusinessException {
		if (AuthTypeUtil.isOtp(authRequestDTO)) {
			AuthStatusInfo otpValidationStatus = null;
			try {
				otpValidationStatus = otpAuthService.authenticate(authRequestDTO, token, Collections.emptyMap(),
						partnerId);
				authStatusList.add(otpValidationStatus);

				boolean isStatus = otpValidationStatus != null && otpValidationStatus.isStatus();
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth), authRequestDTO.getTransactionID(),
						idType, "authenticateApplicant status : " + isStatus);
			} finally {
				boolean isStatus = otpValidationStatus != null && otpValidationStatus.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, EnvUtil.getAppId(),
						AUTH_FACADE, "OTP Authentication status : " + isStatus);
				authTxnBuilder.addRequestType(RequestType.OTP_AUTH);
			}

		}
	}

	/**
	 * Gets the audit event.
	 *
	 * @param isAuth the is auth
	 * @return the audit event
	 */
	private AuditEvents getAuditEvent(boolean isAuth) {
		return isAuth ? AuditEvents.AUTH_REQUEST_RESPONSE : AuditEvents.INTERNAL_REQUEST_RESPONSE;
	}

	/**
	 * Processed to authentic bio type request.
	 *
	 * @param authRequestDTO authRequestDTO
	 * @param uin            the uin
	 * @param idType         idtype
	 * @param isStatus       the is status
	 * @param authTokenId    the auth token id
	 * @param authTxnBuilder
	 * @param exception
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void saveAndAuditBioAuthTxn(AuthRequestDTO authRequestDTO, String token, IdType idType, boolean isStatus,
			String authTokenId, boolean isInternal, String partnerId, AuthTransactionBuilder authTxnBuilder, String idvidHash)
			throws IdAuthenticationBusinessException {
		String status = "authenticateApplicant status : " + isStatus;
		if (AuthTransactionHelper.isFingerAuth(authRequestDTO, env)) {
			auditHelper.audit(AuditModules.FINGERPRINT_AUTH, getAuditEvent(!isInternal),
				authRequestDTO.getTransactionID(), idType, status);
			authTxnBuilder.addRequestType(RequestType.FINGER_AUTH);
		}
		if (AuthTransactionHelper.isIrisAuth(authRequestDTO, env)) {
			auditHelper.audit(AuditModules.IRIS_AUTH, getAuditEvent(!isInternal), authRequestDTO.getTransactionID(),
					idType, status);
			authTxnBuilder.addRequestType(RequestType.IRIS_AUTH);
		}
		if (AuthTransactionHelper.isFaceAuth(authRequestDTO, env)) {
			auditHelper.audit(AuditModules.FACE_AUTH, getAuditEvent(!isInternal), authRequestDTO.getTransactionID(),
					idType, status);
			authTxnBuilder.addRequestType(RequestType.FACE_AUTH);
		}
	}


	/**
	 *
	 * @param authRequestDTO
	 * @param token
	 * @param isAuth
	 * @param authStatusList
	 * @param idType
	 * @param authTokenId
	 * @param partnerId
	 * @param authTxnBuilder
	 * @param idvidHash
	 * @throws IdAuthenticationBusinessException
	 */
	private void processTokenAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String token,
								  boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId,
								  AuthTransactionBuilder authTxnBuilder, String idvidHash) throws IdAuthenticationBusinessException {
		if (AuthTypeUtil.isKeyBindedToken(authRequestDTO)) {
			AuthStatusInfo tokenValidationStatus = null;
			try {
				tokenValidationStatus = keyBindedTokenAuthService.authenticate(authRequestDTO, token, idInfo, partnerId);
				authStatusList.add(tokenValidationStatus);

				boolean isStatus = tokenValidationStatus != null && tokenValidationStatus.isStatus();
				auditHelper.audit(AuditModules.TOKEN_AUTH, getAuditEvent(isAuth), authRequestDTO.getTransactionID(),
						idType, "authenticateApplicant status : " + isStatus);
			} finally {
				boolean isStatus = tokenValidationStatus != null && tokenValidationStatus.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, EnvUtil.getAppId(),
						AUTH_FACADE, "Token Authentication status : " + isStatus);
				authTxnBuilder.addRequestType(RequestType.TOKEN_AUTH);
			}
		}
	}

	/**
	 *
	 * @param authRequestDTO
	 * @param token
	 * @param isAuth
	 * @param authStatusList
	 * @param idType
	 * @param authTokenId
	 * @param partnerId
	 * @param authTxnBuilder
	 * @param idvidHash
	 * @throws IdAuthenticationBusinessException
	 */
	private void processPasswordAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String token,
								  boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId,
								  AuthTransactionBuilder authTxnBuilder, String idvidHash) throws IdAuthenticationBusinessException {
		if (AuthTypeUtil.isPassword(authRequestDTO)) {
			AuthStatusInfo passwordMatchStatus = null;
			try {
				passwordMatchStatus = passwordAuthService.authenticate(authRequestDTO, token, idInfo, partnerId);
				authStatusList.add(passwordMatchStatus);

				boolean isStatus = passwordMatchStatus != null && passwordMatchStatus.isStatus();
				auditHelper.audit(AuditModules.PASSWORD_AUTH, AuditEvents.PASSWORD_BASED_AUTH_REQUEST, authRequestDTO.getTransactionID(),
						idType, "authenticateApplicant status(Password) : " + isStatus);
			} finally {
				boolean isStatus = passwordMatchStatus != null && passwordMatchStatus.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, EnvUtil.getAppId(),
						AUTH_FACADE, "Password Authentication status : " + isStatus);
				authTxnBuilder.addRequestType(RequestType.PASSWORD_AUTH);
			}
		}
	}
	
}
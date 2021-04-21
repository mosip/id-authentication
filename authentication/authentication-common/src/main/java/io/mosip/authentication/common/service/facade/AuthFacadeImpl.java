/*
 * 
 */
package io.mosip.authentication.common.service.facade;

import static io.mosip.authentication.core.constant.AuthTokenType.PARTNER;
import static io.mosip.authentication.core.constant.AuthTokenType.POLICY;
import static io.mosip.authentication.core.constant.AuthTokenType.POLICY_GROUP;
import static io.mosip.authentication.core.constant.AuthTokenType.RANDOM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthResponseBuilder;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.helper.AuthTransactionHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.authtype.dto.AuthtypeStatus;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.authentication.core.spi.authtype.status.service.AuthtypeStatusService;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.match.AuthType;
import io.mosip.authentication.core.spi.indauth.match.IdInfoFetcher;
import io.mosip.authentication.core.spi.indauth.match.MatchType;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
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

	/** The Environment */
	@Autowired
	private Environment env;

	/** The Demo Auth Service */
	@Autowired
	private DemoAuthService demoAuthService;

	/** The BioAuthService */
	@Autowired
	private BioAuthService bioAuthService;

	/** The NotificationService */
	@Autowired
	private NotificationService notificationService;

	/** The Pin Auth Service */
	@Autowired
	private PinAuthService pinAuthService;

	/** The TokenId manager */
	@Autowired
	private TokenIdManager tokenIdManager;

	@Autowired
	private AuthtypeStatusService authTypeStatusService;

	@Autowired
	private IdAuthSecurityManager securityManager;

	@Autowired
	private PartnerService partnerService;

	@Autowired
	private IdInfoFetcher idInfoFetcher;

	@Autowired
	private AuthTransactionHelper authTransactionHelper;
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.AuthFacade#
	 * authenticateApplicant(io.mosip.authentication.core.dto.indauth.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequestDTO, boolean isAuth, String partnerId, String partnerApiKey, boolean markVidConsumed)
			throws IdAuthenticationBusinessException {

		String idvid = authRequestDTO.getIndividualId();
		String idvIdType = IdType.getIDTypeStrOrDefault(authRequestDTO.getIndividualIdType());
		logger.debug(IdAuthCommonConstants.SESSION_ID, "AuthFacedImpl", "authenticateIndividual: ",
				idvIdType + "-" + idvid);

		Map<String, Object> idResDTO = idService.processIdType(idvIdType, idvid,
				authRequestDTO.getRequestedAuth().isBio(), markVidConsumed);
		
		String token = idService.getToken(idResDTO);
		validateAuthTypeStatus(authRequestDTO, token);
		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder
				.newInstance(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String authTokenId = null;
		Boolean authTokenRequired = env.getProperty(IdAuthConfigKeyConstants.RESPONSE_TOKEN_ENABLE, Boolean.class);
		
		AuthTransactionBuilder authTxnBuilder = (AuthTransactionBuilder) authRequestDTO.getMetadata().get(AuthTransactionBuilder.class.getSimpleName());
		authTxnBuilder.withToken(token);
		
		try {
			idInfo = IdInfoFetcher.getIdInfo(idResDTO);
			authResponseBuilder.setTxnID(authRequestDTO.getTransactionID());
			authTokenId = authTokenRequired && isAuth ? getToken(authRequestDTO, partnerId, partnerApiKey, idvid, token) : null;
			
			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, token, isAuth, authTokenId,
					partnerId, authTxnBuilder);
			authStatusList.stream().filter(Objects::nonNull).forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			// Set response token
			if (authTokenRequired) {
				authResponseDTO = authResponseBuilder.build(authTokenId);
			} else {
				authResponseDTO = authResponseBuilder.build(null);
			}
			
			authTxnBuilder.withStatus(authResponseDTO.getResponse().isAuthStatus());
			authTxnBuilder.withAuthToken(authTokenId);
			
			// This is sent back for the consumption by the caller for example
			// KYCFacadeImpl. Whole metadata will be removed at the end by filter.
			authResponseDTO.putMetadata(IdAuthCommonConstants.IDENTITY_DATA, idResDTO);
			
			authTransactionHelper.setAuthTransactionEntityMetadata(authResponseDTO, authTxnBuilder);
			
			logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
					AUTH_FACADE, "authenticateApplicant status : " + authResponseDTO.getResponse().isAuthStatus());
		}

		if (idInfo != null && idvid != null) {
			notificationService.sendAuthNotification(authRequestDTO, idvid, authResponseDTO, idInfo, isAuth);
		}

		return authResponseDTO;

	}

	private String getToken(AuthRequestDTO authRequestDTO, String partnerId, String partnerApiKey, String idvid, String token)
			throws IdAuthenticationBusinessException {
		Optional<PartnerPolicyResponseDTO> policyForPartner = partnerService.getPolicyForPartner(partnerId, partnerApiKey, authRequestDTO.getMetadata());
		Optional<String> authTokenTypeOpt = policyForPartner.map(PartnerPolicyResponseDTO::getPolicy)
				.map(PolicyDTO::getAuthTokenType);
		if (authTokenTypeOpt.isPresent()) {
			String authTokenType = authTokenTypeOpt.get();
			if (authTokenType.equalsIgnoreCase(RANDOM.getType())) {
				return createRandomToken(authRequestDTO.getTransactionID());
			} else if (authTokenType.equalsIgnoreCase(PARTNER.getType())) {
				return tokenIdManager.generateTokenId(token, partnerId);
			} else if(authTokenType.equalsIgnoreCase(POLICY.getType())){
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

	private void validateAuthTypeStatus(AuthRequestDTO authRequestDTO, String token) throws IdAuthenticationBusinessException {
		List<AuthtypeStatus> authtypeStatusList = authTypeStatusService
				.fetchAuthtypeStatus(token);
		if (Objects.nonNull(authtypeStatusList) && !authtypeStatusList.isEmpty()) {
			for (AuthtypeStatus authTypeStatus : authtypeStatusList) {
				validateAuthTypeStatus(authRequestDTO, authTypeStatus);
			}
		}
	}

	private void validateAuthTypeStatus(AuthRequestDTO authRequestDTO, AuthtypeStatus authTypeStatus)
			throws IdAuthenticationBusinessException {
		if (authTypeStatus.getLocked()) {
			if (authRequestDTO.getRequestedAuth().isDemo()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.DEMO.getType())) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
								MatchType.Category.DEMO.getType()));
			}

			else if (authRequestDTO.getRequestedAuth().isBio()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.BIO.getType())) {
				for (AuthType authType : BioAuthType.getSingleBioAuthTypes().toArray(s -> new AuthType[s])) {
					if (authType.getType().equalsIgnoreCase(authTypeStatus.getAuthSubType())) {
						if (authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)) {
							throw new IdAuthenticationBusinessException(
									IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
									String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
											MatchType.Category.BIO.getType() + "-" + authType.getType()));
						} else {
							break;
						}
					}
				}
			}

			else if (authRequestDTO.getRequestedAuth().isOtp()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.OTP.getType())) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
								MatchType.Category.OTP.getType()));
			}

			else if (authRequestDTO.getRequestedAuth().isPin()
					&& authTypeStatus.getAuthType().equalsIgnoreCase(MatchType.Category.SPIN.getType())) {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.AUTH_TYPE_LOCKED.getErrorMessage(),
								MatchType.Category.SPIN.getType()));
			}
		}
	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param idInfo
	 *            list of identityInfoDto request
	 * @param uin
	 *            the uin
	 * @param isAuth
	 *            the is auth
	 * @param authTokenId
	 *            the auth token id
	 * @param partnerId
	 *            the partner id
	 * @return the list
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String token, boolean isAuth, String authTokenId,
			String partnerId, AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {

		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		IdType idType = IdType.getIDTypeOrDefault(authRequestDTO.getIndividualIdType());

		processOTPAuth(authRequestDTO, token, isAuth, authStatusList, idType, authTokenId, partnerId, authTxnBuilder);

		if(!isMatchFailed(authStatusList)) {
			processPinAuth(authRequestDTO, token, authStatusList, idType, authTokenId, isAuth, partnerId, authTxnBuilder);
		}
		
		if(!isMatchFailed(authStatusList)) {
			processDemoAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, authTokenId, partnerId, authTxnBuilder);
		}
		
		if(!isMatchFailed(authStatusList)) {
			processBioAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, authTokenId, partnerId, authTxnBuilder);
		}

		return authStatusList;
	}

	private boolean isMatchFailed(List<AuthStatusInfo> authStatusList) {
		return authStatusList.stream().anyMatch(st -> st != null && !st.isStatus());
	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param uin
	 *            the uin
	 * @param authStatusList
	 *            the auth status list
	 * @param idType
	 *            the id type
	 * @param authTokenId
	 *            the response token id
	 * @param partnerId
	 *            the partner id
	 * @param authTxnBuilder 
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processPinAuth(AuthRequestDTO authRequestDTO, String token, List<AuthStatusInfo> authStatusList,
			IdType idType, String authTokenId, boolean isAuth, String partnerId, AuthTransactionBuilder authTxnBuilder)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isPin()) {
			try {
				AuthStatusInfo pinValidationStatus;
				pinValidationStatus = pinAuthService.authenticate(authRequestDTO, token, Collections.emptyMap(),
						partnerId);
				authStatusList.add(pinValidationStatus);
				statusInfo = pinValidationStatus;

				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				auditHelper.audit(AuditModules.PIN_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
						authRequestDTO.getIndividualId(), idType, "authenticateApplicant status : " + isStatus);

			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "Pin Authentication  status :" + isStatus);
				authTxnBuilder.addRequestType(RequestType.STATIC_PIN_AUTH);
			}

		}
	}

	/**
	 * process the BioAuth.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param idInfo
	 *            the id info
	 * @param uin
	 *            the uin
	 * @param isAuth
	 * @param authStatusList
	 *            the auth status list
	 * @param idType
	 *            the id type
	 * @param authTokenId
	 *            the response token id
	 * @param partnerId
	 *            the partner id
	 * @param authTxnBuilder 
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processBioAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String token,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId, AuthTransactionBuilder authTxnBuilder)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isBio()) {
			AuthStatusInfo bioValidationStatus;
			try {
				bioValidationStatus = bioAuthService.authenticate(authRequestDTO, token, idInfo, partnerId, isAuth);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;

				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				saveAndAuditBioAuthTxn(authRequestDTO, token, idType, isStatus, authTokenId,
						!isAuth, partnerId, authTxnBuilder);
			} finally {
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "BioMetric Authentication status :" + statusInfo);
			}

		}
	}

	/**
	 * Process demo auth.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param idInfo
	 *            the id info
	 * @param uin
	 *            the uin
	 * @param isAuth
	 *            the is auth
	 * @param authStatusList
	 *            the auth status list
	 * @param idType
	 *            the id type
	 * @param authTokenId
	 *            the response token id
	 * @param partnerId
	 *            the partner id
	 * @param authTxnBuilder 
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processDemoAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String token,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId, AuthTransactionBuilder authTxnBuilder)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isDemo()) {
			AuthStatusInfo demoValidationStatus;
			try {
				demoValidationStatus = demoAuthService.authenticate(authRequestDTO, token, idInfo, partnerId);
				authStatusList.add(demoValidationStatus);
				statusInfo = demoValidationStatus;

				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				auditHelper.audit(AuditModules.DEMO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIndividualId(),
						idType, "authenticateApplicant status : " + isStatus);
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();

				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "Demographic Authentication status : " + isStatus);
				authTxnBuilder.addRequestType(RequestType.DEMO_AUTH);
			}

		}
	}

	/**
	 * Process OTP auth.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param uin
	 *            the uin
	 * @param isAuth
	 *            the is auth
	 * @param authStatusList
	 *            the auth status list
	 * @param idType
	 *            the id type
	 * @param authTokenId
	 *            the auth token id
	 * @param partnerId
	 *            the partner id
	 * @param authTxnBuilder 
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processOTPAuth(AuthRequestDTO authRequestDTO, String token, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String authTokenId, String partnerId, AuthTransactionBuilder authTxnBuilder)
			throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequestedAuth().isOtp()) {
			AuthStatusInfo otpValidationStatus = null;
			try {
				otpValidationStatus = otpAuthService.authenticate(authRequestDTO, token, Collections.emptyMap(),
						partnerId);
				authStatusList.add(otpValidationStatus);

				boolean isStatus = otpValidationStatus != null && otpValidationStatus.isStatus();
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth), authRequestDTO.getIndividualId(),
						idType, "authenticateApplicant status : " + isStatus);
			} finally {
				boolean isStatus = otpValidationStatus != null && otpValidationStatus.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "OTP Authentication status : " + isStatus);
				authTxnBuilder.addRequestType(RequestType.OTP_AUTH);
			}

		}
	}

	/**
	 * Gets the audit event.
	 *
	 * @param isAuth
	 *            the is auth
	 * @return the audit event
	 */
	private AuditEvents getAuditEvent(boolean isAuth) {
		return isAuth ? AuditEvents.AUTH_REQUEST_RESPONSE : AuditEvents.INTERNAL_REQUEST_RESPONSE;
	}

	/**
	 * Processed to authentic bio type request.
	 *
	 * @param authRequestDTO
	 *            authRequestDTO
	 * @param uin
	 *            the uin
	 * @param idType
	 *            idtype
	 * @param isStatus
	 *            the is status
	 * @param authTokenId
	 *            the auth token id
	 * @param authTxnBuilder 
	 * @param exception
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void saveAndAuditBioAuthTxn(AuthRequestDTO authRequestDTO, String token, IdType idType, boolean isStatus,
			String authTokenId, boolean isInternal, String partnerId, AuthTransactionBuilder authTxnBuilder) throws IdAuthenticationBusinessException {
		String status = "authenticateApplicant status : " + isStatus;
		if (AuthTransactionHelper.isFingerAuth(authRequestDTO, env)) {
			auditHelper.audit(AuditModules.FINGERPRINT_AUTH, getAuditEvent(!isInternal),
					authRequestDTO.getIndividualId(), idType, status);
			authTxnBuilder.addRequestType(RequestType.FINGER_AUTH);
		}
		if (AuthTransactionHelper.isIrisAuth(authRequestDTO, env)) {
			auditHelper.audit(AuditModules.IRIS_AUTH, getAuditEvent(!isInternal), authRequestDTO.getIndividualId(),
					idType, status);
			authTxnBuilder.addRequestType(RequestType.IRIS_AUTH);
		}
		if (AuthTransactionHelper.isFaceAuth(authRequestDTO, env)) {
			auditHelper.audit(AuditModules.FACE_AUTH, getAuditEvent(!isInternal), authRequestDTO.getIndividualId(),
					idType, status);
			authTxnBuilder.addRequestType(RequestType.FACE_AUTH);
		}
	}

}

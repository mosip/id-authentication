/*
 * 
 */
package io.mosip.authentication.common.service.facade;

import static io.mosip.authentication.core.constant.IdAuthConfigKeyConstants.FMR_ENABLED_TEST;

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
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.common.service.repository.UinEncryptSaltRepo;
import io.mosip.authentication.common.service.repository.UinHashSaltRepo;
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
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.partner.dto.PartnerDTO;
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
	private UinEncryptSaltRepo uinEncryptSaltRepo;

	@Autowired
	private UinHashSaltRepo uinHashSaltRepo;

	@Autowired
	private AuthtypeStatusService authTypeStatusService;

	@Autowired
	private IdAuthSecurityManager securityManager;
	
	@Autowired
	private PartnerService partnerService;
	
	@Autowired
	private IdInfoFetcher idInfoFetcher;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.authentication.core.spi.indauth.facade.AuthFacade#
	 * authenticateApplicant(io.mosip.authentication.core.dto.indauth.
	 * AuthRequestDTO, boolean, java.lang.String)
	 */
	@Override
	public AuthResponseDTO authenticateIndividual(AuthRequestDTO authRequestDTO, boolean isAuth, String partnerId)
			throws IdAuthenticationBusinessException {

		String idvid = authRequestDTO.getIndividualId();
		String idvIdType = IdType.getIDTypeStrOrDefault(authRequestDTO.getIndividualIdType());
		logger.debug(IdAuthCommonConstants.SESSION_ID, "AuthFacedImpl", "authenticateIndividual: ",
				 idvIdType + "-" + idvid);
		
		Map<String, Object> idResDTO = idService.processIdType(idvIdType, idvid,
				authRequestDTO.getRequestedAuth().isBio());
		if (idvIdType.equalsIgnoreCase(IdType.VID.getType())) {
			idService.updateVIDstatus(authRequestDTO.getIndividualId());
		}
		String token = idService.getToken(idResDTO);
		validateAuthTypeStatus(authRequestDTO, token);
		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder
				.newInstance(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String staticTokenId = null;
		Boolean staticTokenRequired = env.getProperty(IdAuthConfigKeyConstants.STATIC_TOKEN_ENABLE, Boolean.class);
		try {
			idInfo = idService.getIdInfo(idResDTO);
			authResponseBuilder.setTxnID(authRequestDTO.getTransactionID());
			staticTokenId = staticTokenRequired && isAuth ? tokenIdManager.generateTokenId(token, partnerId) : null;
			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, token, isAuth, staticTokenId,
					partnerId);
			authStatusList.stream()
						.filter(Objects::nonNull)
						.forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			// Set static token
			if (staticTokenRequired) {
				authResponseDTO = authResponseBuilder.build(staticTokenId);
			} else {
				authResponseDTO = authResponseBuilder.build(null);
			}
			logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
					AUTH_FACADE, "authenticateApplicant status : " + authResponseDTO.getResponse().isAuthStatus());
		}

		if (idInfo != null && token != null) {
			notificationService.sendAuthNotification(authRequestDTO, token, authResponseDTO, idInfo, isAuth);
		}

		return authResponseDTO;

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
					if(authType.getType().equalsIgnoreCase(authTypeStatus.getAuthSubType())) {
						if(authType.isAuthTypeEnabled(authRequestDTO, idInfoFetcher)) {
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
	 * @param staticTokenId
	 *            the static token id
	 * @param partnerId
	 *            the partner id
	 * @return the list
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String token, boolean isAuth, String staticTokenId,
			String partnerId) throws IdAuthenticationBusinessException {

		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		IdType idType = IdType.getIDTypeOrDefault(authRequestDTO.getIndividualIdType());

		processOTPAuth(authRequestDTO, token, isAuth, authStatusList, idType, staticTokenId, partnerId);

		if(!isMatchFailed(authStatusList)) {
			processPinAuth(authRequestDTO, token, authStatusList, idType, staticTokenId, isAuth, partnerId);
		}
		
		if(!isMatchFailed(authStatusList)) {
			processDemoAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, staticTokenId, partnerId);
		}
		
		if(!isMatchFailed(authStatusList)) {
			processBioAuth(authRequestDTO, idInfo, token, isAuth, authStatusList, idType, staticTokenId, partnerId);
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
	 * @param staticTokenId
	 *            the static token id
	 * @param partnerId
	 *            the partner id
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processPinAuth(AuthRequestDTO authRequestDTO, String uin, List<AuthStatusInfo> authStatusList,
			IdType idType, String staticTokenId, boolean isAuth, String partnerId) throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isPin()) {
			try {
				AuthStatusInfo pinValidationStatus;
				pinValidationStatus = pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),
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
				
				AutnTxn authTxn = createAuthTxn(authRequestDTO, uin, isStatus, staticTokenId,
						RequestType.STATIC_PIN_AUTH, !isAuth, partnerId);
				idService.saveAutnTxn(authTxn);
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
	 * @param staticTokenId
	 *            the static token id
	 * @param partnerId
	 *            the partner id
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processBioAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String uin,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isBio()) {
			AuthStatusInfo bioValidationStatus;
			try {
				bioValidationStatus = bioAuthService.authenticate(authRequestDTO, uin, idInfo, partnerId, isAuth);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;
				
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				saveAndAuditBioAuthTxn(authRequestDTO, authRequestDTO.getIndividualId(), idType, isStatus,
						staticTokenId, !isAuth, partnerId);
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
	 * @param staticTokenId
	 *            the static token id
	 * @param partnerId
	 *            the partner id
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processDemoAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String token,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
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

				AutnTxn authTxn = createAuthTxn(authRequestDTO, token, isStatus, staticTokenId, RequestType.DEMO_AUTH, !isAuth, partnerId);
				idService.saveAutnTxn(authTxn);

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
	 * @param staticTokenId
	 *            the static token id
	 * @param partnerId
	 *            the partner id
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processOTPAuth(AuthRequestDTO authRequestDTO, String uin, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequestedAuth().isOtp()) {
			AuthStatusInfo otpValidationStatus = null;
			try {
				otpValidationStatus = otpAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(), partnerId);
				authStatusList.add(otpValidationStatus);
				
				boolean isStatus = otpValidationStatus != null && otpValidationStatus.isStatus();
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth), authRequestDTO.getIndividualId(),
						idType, "authenticateApplicant status : " + isStatus);
			} finally {
				boolean isStatus = otpValidationStatus != null && otpValidationStatus.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "OTP Authentication status : " + isStatus);
				
				AutnTxn authTxn = createAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.OTP_AUTH, !isAuth, partnerId);
				idService.saveAutnTxn(authTxn);
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
	 * @param staticTokenId
	 *            the static token id
	 * @param exception 
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void saveAndAuditBioAuthTxn(AuthRequestDTO authRequestDTO, String uin, IdType idType, boolean isStatus,
			String staticTokenId, boolean isInternal, String partnerId) throws IdAuthenticationBusinessException {
		String status = "authenticateApplicant status : " + isStatus;
		if ((authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.FGR_IMG.getType())
						|| (FMR_ENABLED_TEST.test(env)
								&& bioInfo.getBioType().equals(BioAuthType.FGR_MIN.getType()))))) {
			
			auditHelper.audit(AuditModules.FINGERPRINT_AUTH, getAuditEvent(!isInternal),
					authRequestDTO.getIndividualId(), idType, status);
			
			AutnTxn authTxn = createAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.FINGER_AUTH, isInternal, partnerId);
			idService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.IRIS_IMG.getType()))) {
			
			auditHelper.audit(AuditModules.IRIS_AUTH, getAuditEvent(!isInternal),
					authRequestDTO.getIndividualId(), idType, status);
			
			AutnTxn authTxn = createAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.IRIS_AUTH, isInternal, partnerId);
			idService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.FACE_IMG.getType()))) {
			
			auditHelper.audit(AuditModules.FACE_AUTH, getAuditEvent(!isInternal),
					authRequestDTO.getIndividualId(), idType, status);
			
			AutnTxn authTxn = createAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.FACE_AUTH, isInternal, partnerId);
			idService.saveAutnTxn(authTxn);
		}
	}

	/**
	 * Fetch auth txn.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param token
	 *            the uin
	 * @param isStatus
	 *            the is status
	 * @param staticTokenId
	 *            the static token id
	 * @param requestType
	 *            the request type
	 * @return the autn txn
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private AutnTxn createAuthTxn(AuthRequestDTO authRequestDTO, String token, boolean isStatus, String staticTokenId,
			RequestType requestType, boolean isInternal, String partnerId) throws IdAuthenticationBusinessException {
		Optional<PartnerDTO> partner = isInternal ? Optional.empty() : partnerService.getPartner(partnerId);

		return AuthTransactionBuilder.newInstance()
				.withToken(token)
				.withAuthRequest(authRequestDTO)
				.withRequestType(requestType)
				.withStaticToken(staticTokenId)
				.withInternal(isInternal)
				.withPartner(partner)
				.withStatus(isStatus)
				.build(env, uinEncryptSaltRepo, uinHashSaltRepo, securityManager);
	}

}

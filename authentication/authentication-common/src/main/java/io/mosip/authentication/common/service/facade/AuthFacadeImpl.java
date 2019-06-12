/*
 * 
 */
package io.mosip.authentication.common.service.facade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.service.builder.AuthResponseBuilder;
import io.mosip.authentication.common.service.builder.AuthTransactionBuilder;
import io.mosip.authentication.common.service.entity.AutnTxn;
import io.mosip.authentication.common.service.helper.AuditHelper;
import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.common.service.integration.IdRepoManager;
import io.mosip.authentication.common.service.integration.TokenIdManager;
import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthConfigKeyConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.indauth.dto.ActionableAuthError;
import io.mosip.authentication.core.indauth.dto.AuthError;
import io.mosip.authentication.core.indauth.dto.AuthRequestDTO;
import io.mosip.authentication.core.indauth.dto.AuthResponseDTO;
import io.mosip.authentication.core.indauth.dto.AuthStatusInfo;
import io.mosip.authentication.core.indauth.dto.BioIdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.IdType;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
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
	private OTPAuthService otpService;

	/** The id auth service. */
	@Autowired
	private IdService<AutnTxn> idAuthService;

	/** The id auth service. */
	@Autowired
	private AuditHelper auditHelper;

	/** The Environment */
	@Autowired
	private Environment env;

	/** The Id Info Service */
	@Autowired
	private IdService<AutnTxn> idInfoService;

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
	
	/** The id repo manager. */
	@Autowired
	private IdRepoManager idRepoManager;

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
		String idvIdType =authRequestDTO.getIndividualIdType();
		Map<String, Object> idResDTO = idAuthService.processIdType(idvIdType, idvid,
				authRequestDTO.getRequestedAuth().isBio());
		  if(idvIdType.equalsIgnoreCase(IdType.VID.getType())) {
			  idRepoManager.updateVIDstatus(authRequestDTO.getIndividualId());
		  }
		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder
				.newInstance(env.getProperty(IdAuthConfigKeyConstants.DATE_TIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String uin = String.valueOf(idResDTO.get("uin"));
		String staticTokenId = null;
		Boolean staticTokenRequired = env.getProperty(IdAuthConfigKeyConstants.STATIC_TOKEN_ENABLE, Boolean.class);
		try {
			idInfo = idInfoService.getIdInfo(idResDTO);
			authResponseBuilder.setTxnID(authRequestDTO.getTransactionID());
			staticTokenId = staticTokenRequired && isAuth ? tokenIdManager.generateTokenId(uin, partnerId) : null;
			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, uin, isAuth, staticTokenId,
					partnerId);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
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

		if (idInfo != null && uin != null) {
			notificationService.sendAuthNotification(authRequestDTO, uin, authResponseDTO, idInfo, isAuth);
		}

		return authResponseDTO;

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
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String uin, boolean isAuth, String staticTokenId,
			String partnerId) throws IdAuthenticationBusinessException {

		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		IdType idType = IdType.getIDTypeOrDefault(authRequestDTO.getIndividualIdType());

		processOTPAuth(authRequestDTO, uin, isAuth, authStatusList, idType, staticTokenId, partnerId);

		processDemoAuth(authRequestDTO, idInfo, uin, isAuth, authStatusList, idType, staticTokenId, partnerId);

		processBioAuth(authRequestDTO, idInfo, uin, authStatusList, idType, staticTokenId, partnerId);

		processPinAuth(authRequestDTO, uin, authStatusList, idType, staticTokenId, partnerId);

		return authStatusList;
	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type.
	 * 
	 * @param authRequestDTO
	 * @param uin
	 * @param isAuth
	 * @param authStatusList
	 * @param idType
	 * @throws IdAuthenticationBusinessException
	 */
	private void processPinAuth(AuthRequestDTO authRequestDTO, String uin,
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isPin()) {
			try {
				AuthStatusInfo pinValidationStatus;
				pinValidationStatus = pinAuthService.authenticate(authRequestDTO, uin, Collections.emptyMap(),
						partnerId);
				authStatusList.add(pinValidationStatus);
				statusInfo = pinValidationStatus;
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "Pin Authentication  status :" + isStatus);
				auditHelper.audit(AuditModules.PIN_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
						authRequestDTO.getIndividualId(), idType, AuditModules.PIN_AUTH.getDesc());
				AutnTxn authTxn = fetchAuthTxn(authRequestDTO, uin, isStatus, staticTokenId,
						RequestType.STATIC_PIN_AUTH);
				idAuthService.saveAutnTxn(authTxn);
			}

		}
	}

	/**
	 * process the BioAuth
	 * 
	 * @param authRequestDTO
	 * @param idInfo
	 * @param isAuth
	 * @param authStatusList
	 * @param idType
	 * @throws IdAuthenticationBusinessException
	 */
	private void processBioAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String uin, 
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isBio()) {
			AuthStatusInfo bioValidationStatus;
			try {
				bioValidationStatus = bioAuthService.authenticate(authRequestDTO, uin, idInfo, partnerId);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "BioMetric Authentication status :" + statusInfo);
				saveAndAuditBioAuthTxn(authRequestDTO, authRequestDTO.getIndividualId(), idType,
						isStatus, staticTokenId);
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
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void processDemoAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String uin,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getRequestedAuth().isDemo()) {
			AuthStatusInfo demoValidationStatus;
			try {
				demoValidationStatus = demoAuthService.authenticate(authRequestDTO, uin, idInfo, partnerId);
				authStatusList.add(demoValidationStatus);
				statusInfo = demoValidationStatus;
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "Demographic Authentication status : " + isStatus);
				auditHelper.audit(AuditModules.DEMO_AUTH, getAuditEvent(isAuth),
						authRequestDTO.getIndividualId(), idType, AuditModules.DEMO_AUTH.getDesc());

				AutnTxn authTxn = fetchAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.DEMO_AUTH);
				idAuthService.saveAutnTxn(authTxn);

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
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private void processOTPAuth(AuthRequestDTO authRequestDTO, String uin, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId, String partnerId)
			throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequestedAuth().isOtp()) {
			AuthStatusInfo otpValidationStatus = null;
			try {
				otpValidationStatus = otpService.authenticate(authRequestDTO, uin, Collections.emptyMap(), partnerId);
				authStatusList.add(otpValidationStatus);
			} catch (IdAuthenticationBusinessException e) {
				logger.error(IdAuthCommonConstants.SESSION_ID, e.getClass().toString(), e.getErrorCode(),
						e.getErrorText());
				otpValidationStatus = new AuthStatusInfo();
				otpValidationStatus.setStatus(false);
				AuthError authError;
				if (e.getActionMessage() != null) {
					authError = new ActionableAuthError(e.getErrorCode(), e.getErrorText(), e.getActionMessage());
				} else {
					authError = new AuthError(e.getErrorCode(), e.getErrorText());
				}
				otpValidationStatus.setErr(Collections.singletonList(authError));
				authStatusList.add(otpValidationStatus);
			} finally {
				boolean isStatus = otpValidationStatus != null && otpValidationStatus.isStatus();
				logger.info(IdAuthCommonConstants.SESSION_ID, env.getProperty(IdAuthConfigKeyConstants.APPLICATION_ID),
						AUTH_FACADE, "OTP Authentication status : " + isStatus);
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth),
						authRequestDTO.getIndividualId(), idType, AuditModules.OTP_AUTH.getDesc());
				AutnTxn authTxn = fetchAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.OTP_AUTH);
				idAuthService.saveAutnTxn(authTxn);
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
	 * @param isAuth         boolean value for verify is auth type request or not.
	 * @param idType         idtype
	 * @param isStatus
	 * @throws IdAuthenticationBusinessException
	 */
	private void saveAndAuditBioAuthTxn(AuthRequestDTO authRequestDTO, String uin, IdType idType,
			boolean isStatus, String staticTokenId) throws IdAuthenticationBusinessException {
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.FGR_MIN.getType())
						|| bioInfo.getBioType().equals(BioAuthType.FGR_IMG.getType()))) {
			auditHelper.audit(AuditModules.FINGERPRINT_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					authRequestDTO.getIndividualId(), idType, AuditModules.FINGERPRINT_AUTH.getDesc());
			AutnTxn authTxn = fetchAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.FINGER_AUTH);
			idAuthService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.IRIS_IMG.getType()))) {
			auditHelper.audit(AuditModules.IRIS_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					authRequestDTO.getIndividualId(), idType, AuditModules.IRIS_AUTH.getDesc());
			AutnTxn authTxn = fetchAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.IRIS_AUTH);
			idAuthService.saveAutnTxn(authTxn);
		}
		if (authRequestDTO.getRequest().getBiometrics().stream().map(BioIdentityInfoDTO::getData)
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioAuthType.FACE_IMG.getType()))) {
			auditHelper.audit(AuditModules.FACE_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					authRequestDTO.getIndividualId(), idType, AuditModules.FACE_AUTH.getDesc());
			AutnTxn authTxn = fetchAuthTxn(authRequestDTO, uin, isStatus, staticTokenId, RequestType.FACE_AUTH);
			idAuthService.saveAutnTxn(authTxn);
		}
	}

	private AutnTxn fetchAuthTxn(AuthRequestDTO authRequestDTO, String uin, boolean isStatus, String staticTokenId,
			RequestType requestType) throws IdAuthenticationBusinessException {
		return AuthTransactionBuilder.newInstance()
				.withUin(uin)
				.withAuthRequest(authRequestDTO)
				.withRequestType(requestType)
				.withStaticToken(staticTokenId)
				.withStatus(isStatus)
				.build(env);
	}

}

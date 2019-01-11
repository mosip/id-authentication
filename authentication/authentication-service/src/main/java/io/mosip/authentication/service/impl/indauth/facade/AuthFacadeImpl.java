/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
import io.mosip.authentication.core.dto.indauth.BioType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.KycAuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycResponseDTO;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * This class provides the implementation of AuthFacade.
 *
 * @author Arun Bose
 * 
 * @author Prem Kumar
 */
@Service
public class AuthFacadeImpl implements AuthFacade {

	private static final String DEMO_AUTHENTICATION_REQUESTED = "Demo Authentication requested";

	private static final String OTP_AUTHENTICATION_REQUESTED = "OTP Authentication requested";

	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant STATUS_SUCCESS. */
	private static final String STATUS_SUCCESS = "y";
	/** The Constant IDA. */
	private static final String IDA = "IDA";

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";
	
	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthFacadeImpl.class);

	/** The otp service. */
	@Autowired
	private OTPAuthService otpService;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/** The Kyc Service */
	@Autowired
	private KycService kycService;
	/** The Environment */
	@Autowired
	private Environment env;
	/** The Id Info Service */
	@Autowired
	private IdRepoService idInfoService;
	/** The Demo Auth Service */
	@Autowired
	private DemoAuthService demoAuthService;

	@Autowired
	private AuditHelper auditHelper;

	@Autowired
	private BioAuthService bioAuthService;

	@Autowired
	private NotificationService notificationService;

	/**
	 * Process the authorization type and authorization response is returned.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param isAuth         boolean i.e is auth type request.
	 * @return AuthResponseDTO the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception.
	 */
	@Override
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO, boolean isAuth)
			throws IdAuthenticationBusinessException {

		Map<String, Object> idResDTO = idAuthService.processIdType(authRequestDTO.getIdvIdType(),
				authRequestDTO.getIdvId());

		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance(env.getProperty(DATETIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String uin = String.valueOf(idResDTO.get("uin"));
		try {
			idInfo = getIdEntity(idResDTO);

			authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIdvIdType())
					.setReqTime(authRequestDTO.getReqTime());

			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, uin, isAuth);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			authResponseDTO = authResponseBuilder.build();
			logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE,
					"authenticateApplicant status : " + authResponseDTO.getStatus());
			if (idInfo != null && uin != null) {
				notificationService.sendAuthNotification(authRequestDTO, uin, authResponseDTO, idInfo, isAuth);
			}
			
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
	 * @param uin the uin
	 * @param isAuth the is auth
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String uin, boolean isAuth)
			throws IdAuthenticationBusinessException {
	
		
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		IdType idType = null;

		if (authRequestDTO.getIdvIdType().equals(IdType.UIN.getType())) {
			idType = IdType.UIN;
		} else {
			idType = IdType.VID;
		}
		
		processOTPAuth(authRequestDTO, uin, isAuth, authStatusList, idType);

		processDemoAuth(authRequestDTO, idInfo, uin, isAuth, authStatusList, idType);
		
		processBioAuth(authRequestDTO, idInfo, isAuth, authStatusList, idType);

		return authStatusList;
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
	private void processBioAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth,
			List<AuthStatusInfo> authStatusList,
			IdType idType) throws IdAuthenticationBusinessException {
	
		
		
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getAuthType().isBio()) {
			AuthStatusInfo bioValidationStatus;
			try {

				bioValidationStatus = bioAuthService.validateBioDetails(authRequestDTO, idInfo);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;
			} finally {
				
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				
				
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "BioMetric Authentication status :" + statusInfo);
				saveAndAuditBioAuthTxn(authRequestDTO, isAuth, idType,isStatus);
				
				
			}
		}
	}

	/**
	 * Process demo auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo the id info
	 * @param uin the uin
	 * @param isAuth the is auth
	 * @param authStatusList the auth status list
	 * @param idType the id type
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void processDemoAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String uin,
			boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType)
			throws IdAuthenticationBusinessException {
		String idvId = authRequestDTO.getIdvId();
		String idvIdType = authRequestDTO.getIdvIdType();
		String reqTime = authRequestDTO.getReqTime();
		String txnId = authRequestDTO.getTxnID();
		
		String status;
		String comment;
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getAuthType().isPersonalIdentity() || authRequestDTO.getAuthType().isAddress()
				|| authRequestDTO.getAuthType().isFullAddress()) {
			AuthStatusInfo demoValidationStatus;
			try {
				demoValidationStatus = demoAuthService.getDemoStatus(authRequestDTO, uin, idInfo);
				authStatusList.add(demoValidationStatus);
				statusInfo = demoValidationStatus;
			} finally {
				
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				status = isStatus ? "Y" : "N";
				
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "Demographic Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.DEMO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType,
						DEMO_AUTHENTICATION_REQUESTED);
				
				comment = isStatus ? "Demo  Authenticated Success" : "Demo  Authenticated Failed";
				idAuthService.saveAutnTxn(idvId, idvIdType, reqTime, txnId, status, comment, RequestType.DEMO_AUTH);
			}

		}
	}

	/**
	 * Process OTP auth.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param uin the uin
	 * @param isAuth the is auth
	 * @param authStatusList the auth status list
	 * @param idType the id type
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	private void processOTPAuth(AuthRequestDTO authRequestDTO, String uin, boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType)
			throws IdAuthenticationBusinessException {
		
		String idvId = authRequestDTO.getIdvId();
		String idvIdType = authRequestDTO.getIdvIdType();
		String reqTime = authRequestDTO.getReqTime();
		String txnId = authRequestDTO.getTxnID();
		
		String status;
		String comment;
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus;
			try {

				otpValidationStatus = otpService.validateOtp(authRequestDTO, uin);
				authStatusList.add(otpValidationStatus);
				statusInfo = otpValidationStatus;
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				status = isStatus ? "Y" : "N";
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "OTP Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth), idvIdType, idType,
						OTP_AUTHENTICATION_REQUESTED);
				
				comment = isStatus ? "OTP Authenticated Success" : "OTP Authenticated Failed";
				idAuthService.saveAutnTxn(idvId, idvIdType, reqTime, txnId, status, comment, RequestType.OTP_AUTH);
			}

		}
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
	private void saveAndAuditBioAuthTxn(AuthRequestDTO authRequestDTO, boolean isAuth, IdType idType, boolean isStatus)
			throws IdAuthenticationBusinessException {
		
		String idvId = authRequestDTO.getIdvId();
		String idvIdType = authRequestDTO.getIdvIdType();
		String reqTime = authRequestDTO.getReqTime();
		String txnId = authRequestDTO.getTxnID();
		String desc;
		String comment;
		String status;
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.FGRMIN.getType())
						|| bioInfo.getBioType().equals(BioType.FGRIMG.getType()))) {
			desc = "Fingerprint Authentication requested";
			auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType, desc);
			status = isStatus ? "Y" : "N";
			comment = isStatus ? "Finger  Authentication Success" : "Finger  Authentication Failed";

			idAuthService.saveAutnTxn(idvId, idvIdType, reqTime, txnId, status, comment, RequestType.FINGER_AUTH);
		}
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.IRISIMG.getType()))) {
			desc = "Iris Authentication requested";
			auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType, desc);
			status = isStatus ? "Y" : "N";
			comment = isStatus ? "Iris  Authentication Success" : "Iris  Authentication Failed";
			idAuthService.saveAutnTxn(idvId, idvIdType,  reqTime, txnId, status, comment, RequestType.IRIS_AUTH);
		}
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.FACEIMG.getType()))) {
			desc = "Face Authentication requested";
			auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType, desc);
			status = isStatus ? "Y" : "N";
			comment = isStatus ? "Face  Authentication Success" : "Face  Authentication Failed";
			idAuthService.saveAutnTxn(idvId, idvIdType,  reqTime, txnId, status, comment, RequestType.FACE_AUTH);
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

	/* (non-Javadoc)
	 * @see io.mosip.authentication.core.spi.indauth.facade.AuthFacade#authenticateTsp(io.mosip.authentication.core.dto.indauth.AuthRequestDTO)
	 */
	@Override
	public AuthResponseDTO authenticateTsp(AuthRequestDTO authRequestDTO) {

		String dateTimePattern = env.getProperty(DATETIME_PATTERN);

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(authRequestDTO.getReqTime(), isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		String resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
		AuthResponseDTO authResponseTspDto = new AuthResponseDTO();
		authResponseTspDto.setStatus(STATUS_SUCCESS);
		authResponseTspDto.setErr(Collections.emptyList());
		authResponseTspDto.setResTime(resTime);
		authResponseTspDto.setTxnID(authRequestDTO.getTxnID());
		return authResponseTspDto;
	}

	/**
	 * Process the KycAuthRequestDTO to integrate with KycService.
	 *
	 * @param kycAuthRequestDTO is DTO of KycAuthRequestDTO
	 * @param authResponseDTO the auth response DTO
	 * @return the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	@Override
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO)
			throws IdAuthenticationBusinessException {
		AuthRequestDTO authRequest = kycAuthRequestDTO.getAuthRequest();
		Map<String, Object> idResDTO = null;
		String key = null;
		String resTime = null;
		IdType idType = null;
		if (authRequest != null) {
			idResDTO = idAuthService.processIdType(authRequest.getIdvIdType(), authRequest.getIdvId());
			key = "ekyc.mua.accesslevel." + kycAuthRequestDTO.getAuthRequest().getTspID();

			if (kycAuthRequestDTO.getAuthRequest().getIdvIdType().equals(IdType.UIN.getType())) {
				idType = IdType.UIN;
			} else {
				idType = IdType.VID;
			}
			String dateTimePattern = env.getProperty(DATETIME_PATTERN);

			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(kycAuthRequestDTO.getAuthRequest().getReqTime(),
					isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
			auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE,
					kycAuthRequestDTO.getAuthRequest().getIdvId(), idType, "eKYC Authentication requested");
		}
		Map<String, List<IdentityInfoDTO>> idInfo = getIdEntity(idResDTO);
		KycInfo info = null;
		if (idResDTO != null) {
			info = kycService.retrieveKycInfo(String.valueOf(idResDTO.get("uin")),
					KycType.getEkycAuthType(env.getProperty(key)), kycAuthRequestDTO.isEPrintReq(),
					kycAuthRequestDTO.isSecLangReq(), idInfo);

		}

		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();

		KycResponseDTO response = new KycResponseDTO();
		response.setAuth(authResponseDTO);
		kycAuthResponseDTO.setResponse(response);
		kycAuthResponseDTO.getResponse().setKyc(info);
		kycAuthResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));

		kycAuthResponseDTO.setStatus(authResponseDTO.getStatus());

		kycAuthResponseDTO.setResTime(resTime);

		return kycAuthResponseDTO;
	}

	/**
	 * Gets the demo entity.
	 *
	 * @param idResponseDTO the id response DTO
	 * @return the demo entity
	 * @throws IdAuthenticationBusinessException the id authentication business exception
	 */
	public Map<String, List<IdentityInfoDTO>> getIdEntity(Map<String, Object> idResponseDTO)
			throws IdAuthenticationBusinessException {
		return idInfoService.getIdInfo(idResponseDTO);
	}

}

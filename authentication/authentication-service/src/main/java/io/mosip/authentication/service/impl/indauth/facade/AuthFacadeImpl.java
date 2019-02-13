/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
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
import io.mosip.authentication.core.spi.indauth.service.PinAuthService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.kernel.core.idgenerator.spi.TokenIdGenerator;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;

/**
 * This class provides the implementation of AuthFacade.
 *
 * @author Arun Bose
 * 
 * @author Prem Kumar
 */
@Service
public class AuthFacadeImpl implements AuthFacade {

	private static final String FAILED = "N";

	/** The Constant UTC. */
	private static final String UTC = "UTC";

	/** The Constant MOSIP_PRIMARY_LANG_CODE. */
	private static final String MOSIP_PRIMARY_LANG_CODE = "mosip.primary.lang-code";

	/** The Constant DATETIME_PATTERN. */
	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant IDA. */
	private static final String IDA = "IDA";

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The Constant SUCCESS_STATUS. */
	private static final String SUCCESS_STATUS = "Y";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthFacadeImpl.class);

	/** The otp service. */
	@Autowired
	private OTPAuthService otpService;

	/** The id auth service. */
	@Autowired
	private IdAuthService<AutnTxn> idAuthService;

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

	/** The AuditHelper */
	@Autowired
	private AuditHelper auditHelper;

	/** The BioAuthService */
	@Autowired
	private BioAuthService bioAuthService;

	/** The NotificationService */
	@Autowired
	private NotificationService notificationService;

	/** The Pin Auth Service */
	@Autowired
	private PinAuthService pinAuthService;

	/** The TokenId Generator */
	@Autowired
	private TokenIdGenerator<String, String> tokenIdGenerator;

	/**
	 * Process the authorization type and authorization response is returned.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param isAuth
	 *            boolean i.e is auth type request.
	 * @return AuthResponseDTO the auth response DTO
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception.
	 */
	@Override
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO, boolean isAuth)
			throws IdAuthenticationBusinessException {

		Map<String, Object> idResDTO = idAuthService.processIdType(authRequestDTO.getIdvIdType(),
				authRequestDTO.getIdvId(), authRequestDTO.getAuthType().isBio());

		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance(env.getProperty(DATETIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String uin = String.valueOf(idResDTO.get("uin"));
		String tspId = authRequestDTO.getTspID();
		try {
			idInfo = idInfoService.getIdInfo(idResDTO);
			authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIdvIdType())
					.setReqTime(authRequestDTO.getReqTime());
			String staticTokenId = tokenIdGenerator.generateId(tspId, uin);
			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, uin, isAuth, staticTokenId);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
			// Set static token
			authResponseBuilder.setStaticTokenId(staticTokenId);
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
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param idInfo
	 *            list of identityInfoDto request
	 * @param uin
	 *            the uin
	 * @param isAuth
	 *            the is auth
	 * @return the list
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String uin, boolean isAuth, String staticTokenId)
			throws IdAuthenticationBusinessException {

		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		IdType idType = null;

		if (authRequestDTO.getIdvIdType().equals(IdType.UIN.getType())) {
			idType = IdType.UIN;
		} else {
			idType = IdType.VID;
		}

		processOTPAuth(authRequestDTO, uin, isAuth, authStatusList, idType, staticTokenId);

		processDemoAuth(authRequestDTO, idInfo, uin, isAuth, authStatusList, idType, staticTokenId);

		processBioAuth(authRequestDTO, idInfo, uin, isAuth, authStatusList, idType, staticTokenId);

		processPinAuth(authRequestDTO, uin, isAuth, authStatusList, idType, staticTokenId);

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
	private void processPinAuth(AuthRequestDTO authRequestDTO, String uin, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId)
			throws IdAuthenticationBusinessException {
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getAuthType().isPin()) {
			AuthStatusInfo pinValidationStatus;
			try {

				pinValidationStatus = pinAuthService.validatePin(authRequestDTO, uin);
				authStatusList.add(pinValidationStatus);
				statusInfo = pinValidationStatus;
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "Pin Authentication  status :" + statusInfo);
				auditHelper.audit(AuditModules.PIN_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, authRequestDTO.getIdvId(),
						idType, AuditModules.PIN_AUTH.getDesc());
				AutnTxn auth_txn = createAuthTxn(authRequestDTO, uin, RequestType.STATIC_PIN_AUTH, staticTokenId,
						isStatus);
				idAuthService.saveAutnTxn(auth_txn);
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
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId)
			throws IdAuthenticationBusinessException {

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
				saveAndAuditBioAuthTxn(authRequestDTO, isAuth, uin, idType, isStatus, staticTokenId);

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
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processDemoAuth(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String uin,
			boolean isAuth, List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId)
			throws IdAuthenticationBusinessException {
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

				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "Demographic Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.DEMO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType,
						AuditModules.DEMO_AUTH.getDesc());

				AutnTxn auth_txn = createAuthTxn(authRequestDTO, uin, RequestType.DEMO_AUTH, staticTokenId, isStatus);
				idAuthService.saveAutnTxn(auth_txn);
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
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	private void processOTPAuth(AuthRequestDTO authRequestDTO, String uin, boolean isAuth,
			List<AuthStatusInfo> authStatusList, IdType idType, String staticTokenId)
			throws IdAuthenticationBusinessException {

		String idvIdType = authRequestDTO.getIdvIdType();
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus;
			try {

				otpValidationStatus = otpService.validateOtp(authRequestDTO, uin);
				authStatusList.add(otpValidationStatus);
				statusInfo = otpValidationStatus;
			} finally {
				boolean isStatus = statusInfo != null && statusInfo.isStatus();
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "OTP Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth), idvIdType, idType,
						AuditModules.OTP_AUTH.getDesc());

				AutnTxn auth_txn = createAuthTxn(authRequestDTO, uin, RequestType.OTP_AUTH, staticTokenId, isStatus);
				idAuthService.saveAutnTxn(auth_txn);
			}

		}
	}

	/**
	 * Processed to authentic bio type request.
	 * 
	 * @param authRequestDTO
	 *            authRequestDTO
	 * @param isAuth
	 *            boolean value for verify is auth type request or not.
	 * @param idType
	 *            idtype
	 * @param isStatus
	 * @throws IdAuthenticationBusinessException
	 */
	private void saveAndAuditBioAuthTxn(AuthRequestDTO authRequestDTO, boolean isAuth, String uin, IdType idType,
			boolean isStatus, String staticTokenId) throws IdAuthenticationBusinessException {
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.FGRMIN.getType())
						|| bioInfo.getBioType().equals(BioType.FGRIMG.getType()))) {
			auditHelper.audit(AuditModules.FINGERPRINT_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType,
					AuditModules.FINGERPRINT_AUTH.getDesc());
			AutnTxn auth_txn = createAuthTxn(authRequestDTO, uin, RequestType.FINGER_AUTH, staticTokenId, isStatus);
			idAuthService.saveAutnTxn(auth_txn);
		}
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.IRISIMG.getType()))) {
			auditHelper.audit(AuditModules.IRIS_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType,
					AuditModules.IRIS_AUTH.getDesc());
			AutnTxn auth_txn = createAuthTxn(authRequestDTO, uin, RequestType.IRIS_AUTH, staticTokenId, isStatus);
			idAuthService.saveAutnTxn(auth_txn);
		}
		if (authRequestDTO.getBioInfo().stream()
				.anyMatch(bioInfo -> bioInfo.getBioType().equals(BioType.FACEIMG.getType()))) {
			auditHelper.audit(AuditModules.FACE_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(), idType,
					AuditModules.FACE_AUTH.getDesc());
			AutnTxn auth_txn = createAuthTxn(authRequestDTO, uin, RequestType.FACE_AUTH, staticTokenId, isStatus);
			idAuthService.saveAutnTxn(auth_txn);
		}
	}

	/**
	 * sets AuthTxn entity values
	 * 
	 * @param authRequestDTO
	 * @param uin
	 * @param status
	 * @param comment
	 * @param requestType
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private AutnTxn createAuthTxn(AuthRequestDTO authRequestDTO, String uin, RequestType requestType,
			String staticTokenId, boolean isStatus) throws IdAuthenticationBusinessException {
		try {
			String status = isStatus ? SUCCESS_STATUS : FAILED;
			String comment = isStatus ? requestType.getMessage() + "Success" : requestType.getMessage() + "Failed";
			String idvId = authRequestDTO.getIdvId();
			String reqTime = authRequestDTO.getReqTime();
			String idvIdType = authRequestDTO.getIdvIdType();
			String txnID = authRequestDTO.getTxnID();
			AutnTxn autnTxn = new AutnTxn();
			autnTxn.setRefId(idvId);
			autnTxn.setRefIdType(idvIdType);
			String id = createId(uin);
			autnTxn.setId(id); // FIXME
			autnTxn.setCrBy(IDA);
			autnTxn.setStaticTknId(staticTokenId);
			autnTxn.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			Date reqDate = null;
			reqDate = DateUtils.parseToDate(reqTime, env.getProperty(DATETIME_PATTERN));
			String dateTimePattern = env.getProperty(DATETIME_PATTERN);
			DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);
			LocalDateTime utcLocalDateTime = DateUtils.parseDateToLocalDateTime(reqDate);
			ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(reqTime, isoPattern);
			ZoneId zone = zonedDateTime2.getZone();
			ZonedDateTime ldtZoned = utcLocalDateTime.atZone(zone);
			ZonedDateTime utcDateTime = ldtZoned.withZoneSameInstant(ZoneId.of(UTC));
			LocalDateTime localDateTime = utcDateTime.toLocalDateTime();
			autnTxn.setRequestDTtimes(localDateTime);
			autnTxn.setResponseDTimes(DateUtils.getUTCCurrentDateTime()); // TODO check this
			autnTxn.setAuthTypeCode(requestType.getRequestType());
			autnTxn.setRequestTrnId(txnID);
			autnTxn.setStatusCode(status);
			autnTxn.setStatusComment(comment);
			// FIXME
			autnTxn.setLangCode(env.getProperty(MOSIP_PRIMARY_LANG_CODE));
			return autnTxn;
		} catch (ParseException e) {
			logger.error(DEFAULT_SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST_TIMESTAMP,
					e);
		}
	}

	/**
	 * Creates UUID
	 * 
	 * @param uin
	 * @return
	 */
	private String createId(String uin) {
		String currentDate = DateUtils.formatDate(new Date(), env.getProperty(DATETIME_PATTERN));
		String uinAndDate = uin + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, uinAndDate).toString();
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
	 * Process the KycAuthRequestDTO to integrate with KycService.
	 *
	 * @param kycAuthRequestDTO
	 *            is DTO of KycAuthRequestDTO
	 * @param authResponseDTO
	 *            the auth response DTO
	 * @return the kyc auth response DTO
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
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
			idResDTO = idAuthService.processIdType(authRequest.getIdvIdType(), authRequest.getIdvId(), true);
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
					kycAuthRequestDTO.getAuthRequest().getIdvId(), idType, AuditModules.EKYC_AUTH.getDesc());
		}
		Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(idResDTO);
		KycInfo info = null;
		if (idResDTO != null && authResponseDTO.getStatus().equals(SUCCESS_STATUS)) {
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
		kycAuthResponseDTO.setErr(authResponseDTO.getErr());
		kycAuthResponseDTO.setStatus(authResponseDTO.getStatus());
		kycAuthResponseDTO.setResTime(resTime);
		return kycAuthResponseDTO;
	}

}

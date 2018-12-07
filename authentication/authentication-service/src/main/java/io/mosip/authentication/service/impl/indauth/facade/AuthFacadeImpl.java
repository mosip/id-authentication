/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.mosip.authentication.core.constant.AuditEvents;
import io.mosip.authentication.core.constant.AuditModules;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
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
import io.mosip.authentication.core.dto.indauth.SenderType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.BioAuthService;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.service.helper.AuditHelper;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdRepoServiceImpl;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoAuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.repository.UinRepository;
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
	/** The Constant STATUS. */
	private static final String STATUS = "status";
	/** The Constant AUTH_TYPE. */
	private static final String AUTH_TYPE = "authType";
	/** The Constant NAME. */
	private static final String NAME = "name";
	/** The Constant UIN2. */
	private static final String UIN2 = "uin";
	/** The Constant TIME. */
	private static final String TIME = "time";
	/** The Constant DATE. */
	private static final String DATE = "date";

	/** The Constant AUTH_FACADE. */
	private static final String AUTH_FACADE = "AuthFacade";

	/** The Constant DEFAULT_SESSION_ID. */
	private static final String DEFAULT_SESSION_ID = "sessionId";

	/** The logger. */
	private static Logger logger = IdaLogger.getLogger(AuthFacadeImpl.class);

	/** The otp service. */
	@Autowired
	private OTPAuthService otpService;

	/** The demo auth service. */
	@Autowired
	private IdInfoHelper demoHelper;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/** The Kyc Service */
	@Autowired
	private KycService kycService;
	/** The Environment */
	@Autowired
	private Environment env;
	/** The Notification Manager */
	@Autowired
	private NotificationManager notificationManager;
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
	private IdRepoServiceImpl idRepoServiceImpl;

	@Autowired
	UinRepository uinRepository;

	/**
	 * Process the authorisation type and authorisation response is returned.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 * @throws IdAuthenticationDaoException
	 * @throws ParseException
	 */

	@Override
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO, boolean isAuth)
			throws IdAuthenticationBusinessException {

		Map<String, Object> idResDTO = processIdType(authRequestDTO);

		// new by rakesh
//		Map<String, Object> idRepoResponse = processIdRepoRequest(authRequestDTO);
		
		
		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = new AuthResponseBuilder(env.getProperty(DATETIME_PATTERN));
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		try {
			idInfo = getIdEntity(idResDTO);

			authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIdvIdType())
					.setReqTime(authRequestDTO.getReqTime()).setVersion(authRequestDTO.getVer());

			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, String.valueOf(idResDTO.get("registrationId")), isAuth);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			authResponseDTO = authResponseBuilder.build();
			logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE,
					"authenticateApplicant status : " + authResponseDTO.getStatus());
			if (idInfo != null) {
				//sendAuthNotification(authRequestDTO, idRepoResponse.getRegistrationId(), authResponseDTO, idInfo, isAuth);
			}
		}

		return authResponseDTO;

	}

	private void sendAuthNotification(AuthRequestDTO authRequestDTO, String refId, AuthResponseDTO authResponseDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, boolean isAuth) throws IdAuthenticationBusinessException {

		boolean ismaskRequired = Boolean.parseBoolean(env.getProperty("uin.masking.required"));

		Map<String, Object> values = new HashMap<>();
		values.put(NAME, demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo));
		String resTime = authResponseDTO.getResTime();

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(authRequestDTO.getReqTime(), isoPattern);
		ZoneId zone = zonedDateTime2.getZone();

		ZonedDateTime dateTimeReq = ZonedDateTime.parse(resTime, isoPattern);
		ZonedDateTime dateTimeConvertedToReqZone = dateTimeReq.withZoneSameInstant(zone);
		String changedDate = dateTimeConvertedToReqZone
				.format(DateTimeFormatter.ofPattern(env.getProperty("notification.date.format")));
		String changedTime = dateTimeConvertedToReqZone
				.format(DateTimeFormatter.ofPattern(env.getProperty("notification.time.format")));

		values.put(DATE, changedDate);
		values.put(TIME, changedTime);
		Optional<String> uinOpt = idAuthService.getUIN(refId);
		String uin = "";

		if (uinOpt.isPresent()) {
			uin = uinOpt.get();
			if (ismaskRequired) {
				uin = MaskUtil.generateMaskValue(uin, Integer.parseInt(env.getProperty("uin.masking.charcount")));
			}
		}

		values.put(UIN2, uin);
		values.put(AUTH_TYPE,

				Stream.of(DemoAuthType.values()).filter(authType -> authType.isAuthTypeEnabled(authRequestDTO))
						.map(DemoAuthType::getDisplayName).distinct().collect(Collectors.joining(",")));
		if (authResponseDTO.getStatus().equalsIgnoreCase(STATUS_SUCCESS)) {
			values.put(STATUS, "Passed");
		} else {
			values.put(STATUS, "Failed");
		}

		String phoneNumber = null;
		String email = null;
		phoneNumber = demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo);
		email = demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo);
		String notificationType = null;
		if (isAuth) {
			notificationType = env.getProperty("auth.notification.type");
		} else {
			notificationType = env.getProperty("internal.auth.notification.type");
		}

		notificationManager.sendNotification(values, email, phoneNumber, SenderType.AUTH, notificationType);
	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @param idInfo
	 * @param refId          the ref id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO,
			Map<String, List<IdentityInfoDTO>> idInfo, String refId, boolean isAuth) throws IdAuthenticationBusinessException {
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo statusInfo = null;
		IdType idType=null;

		if(authRequestDTO.getIdvIdType().equals(IdType.UIN.getType()))	{
			idType=IdType.UIN;
		}
		else{
			 idType=IdType.VID;
		}
		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus;
			try {

				otpValidationStatus = otpService.validateOtp(authRequestDTO, refId);
				authStatusList.add(otpValidationStatus);
				statusInfo = otpValidationStatus;
			} finally {
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "OTP Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.OTP_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(),idType, OTP_AUTHENTICATION_REQUESTED);
			}

		}

		if (authRequestDTO.getAuthType().isPersonalIdentity() || authRequestDTO.getAuthType().isAddress()
				|| authRequestDTO.getAuthType().isFullAddress()) {
			AuthStatusInfo demoValidationStatus;
			try {
				demoValidationStatus = demoAuthService.getDemoStatus(authRequestDTO, refId, idInfo);
				authStatusList.add(demoValidationStatus);
				statusInfo = demoValidationStatus;
			} finally {
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "Demographic Authentication status : " + statusInfo);
				auditHelper.audit(AuditModules.DEMO_AUTH,  getAuditEvent(isAuth), authRequestDTO.getIdvId(),idType, DEMO_AUTHENTICATION_REQUESTED);
			}

		}
		if (authRequestDTO.getAuthType().isBio()) {
			AuthStatusInfo bioValidationStatus;
			try {

				// TODO log authStatus - authType, response
				bioValidationStatus = bioAuthService.validateBioDetails(authRequestDTO, idInfo, refId);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;
			} finally {
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "BioMetric Authentication status :" + statusInfo);
				String desc;
				if(authRequestDTO.getBioInfo().stream().anyMatch(bioInfo -> bioInfo.equals(BioType.FGRMIN.getType()) || bioInfo.equals(BioType.FGRIMG.getType()))) {
					desc = "Fingerprint Authentication requested";
					auditHelper.audit(AuditModules.BIO_AUTH,  getAuditEvent(isAuth), authRequestDTO.getIdvId(),idType,desc );
				}
				if(authRequestDTO.getBioInfo().stream().anyMatch(bioInfo -> bioInfo.equals(BioType.IRISIMG.getType()))) {
					desc = "Iris Authentication requested";
					auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(),idType,desc );
				}
				if(authRequestDTO.getBioInfo().stream().anyMatch(bioInfo -> bioInfo.equals(BioType.FACEIMG.getType()))) {
					desc = "Face Authentication requested";
					auditHelper.audit(AuditModules.BIO_AUTH, getAuditEvent(isAuth), authRequestDTO.getIdvId(),idType,desc );
				}
			}
		}

		return authStatusList;
	}

	private AuditEvents getAuditEvent(boolean isAuth) {
		return isAuth ? AuditEvents.AUTH_REQUEST_RESPONSE : AuditEvents.INTERNAL_REQUEST_RESPONSE;
	}

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 *
	 * @param authRequestDTO the auth request DTO
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public Map<String, Object> processIdType(AuthRequestDTO authRequestDTO) throws IdAuthenticationBusinessException {
		Map<String, Object> idResDTO = null;
		String reqType = authRequestDTO.getIdvIdType();
		if (reqType.equals(IdType.UIN.getType())) {
			try {
				idResDTO = idAuthService.validateUIN(authRequestDTO.getIdvId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
			}
		} else {
			try {
				//FIXME handle this
				String refId = idAuthService.validateVID(authRequestDTO.getIdvId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, null, e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
			}
		}

		return idResDTO;
	}

	/**
	 * Audit data.
	 */
	private void auditData() {
		// TODO Update audit details

	}

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
	 * 
	 * Process the KycAuthRequestDTO to integrate with KycService
	 * 
	 * @param kycAuthRequestDTO is DTO of KycAuthRequestDTO
	 * 
	 */
	@Override
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO)
			throws IdAuthenticationBusinessException {
		Map<String, Object> idResDTO = processIdType(kycAuthRequestDTO.getAuthRequest());
		String key = "ekyc.mua.accesslevel." + kycAuthRequestDTO.getAuthRequest().getMuaCode();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdEntity(idResDTO);
		KycInfo info = kycService.retrieveKycInfo(String.valueOf(idResDTO.get("registrationId")), KycType.getEkycAuthType(env.getProperty(key)),
				kycAuthRequestDTO.isEPrintReq(), kycAuthRequestDTO.isSecLangReq(), idInfo);
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();

		KycResponseDTO response = new KycResponseDTO();
		response.setAuth(authResponseDTO);
		kycAuthResponseDTO.setResponse(response);
		kycAuthResponseDTO.getResponse().setKyc(info);
		kycAuthResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));

		kycAuthResponseDTO.setStatus(authResponseDTO.getStatus());
		// String resTime = new
		// SimpleDateFormat(env.getProperty(DATETIME_PATTERN)).format(new Date());
		String dateTimePattern = env.getProperty(DATETIME_PATTERN);

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(dateTimePattern);

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(kycAuthRequestDTO.getAuthRequest().getReqTime(), isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		String resTime = DateUtils.formatDate(new Date(), dateTimePattern, TimeZone.getTimeZone(zone));
		kycAuthResponseDTO.setResTime(resTime);
		IdType idType;

		if(kycAuthRequestDTO.getAuthRequest().getIdvIdType()==IdType.UIN.getType())	{
			idType=IdType.UIN;
		}
		else{
			 idType=IdType.VID;
		}
		auditHelper.audit(AuditModules.EKYC_AUTH, AuditEvents.AUTH_REQUEST_RESPONSE, kycAuthRequestDTO.getAuthRequest().getIdvId(),idType,"" );
		return kycAuthResponseDTO;
	}

	/**
	 * Gets the demo entity.
	 *
	 * @param uniqueId the unique id
	 * @return the demo entity
	 * @throws IdAuthenticationBusinessException
	 * @throws IdAuthenticationDaoException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public Map<String, List<IdentityInfoDTO>> getIdEntity(Map<String, Object> idResponseDTO) throws IdAuthenticationBusinessException {

		try {
			return idInfoService.getIdInfo(idResponseDTO);
		} catch (IdAuthenticationDaoException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}

	}

//	private Map<String, Object> processIdRepoRequest(AuthRequestDTO authRequestDTO) throws IdAuthenticationBusinessException {
//		Map<String, Object> idRepo = null;
//
//		String reqType = authRequestDTO.getIdvIdType();
//
//		if (reqType.equals(IdType.UIN.getType())) {
//			idRepo = idRepoServiceImpl.getIdRepo(authRequestDTO.getIdvId());
//
//		} else {
//			// Optional<String> findRefIdByVid =
//			// vidRepository.findRefIdByVid(authRequestDTO.getIdvId());
//			// if (findRefIdByVid.isPresent()) {
//
//			// String refId = findRefIdByVid.get();
//			// Optional<String> findUinByRefId = uinRepository.findUinByRefId(refId);
//
//			// if (findUinByRefId.isPresent()) {
//			// String uin = findUinByRefId.get();
//			// idRepo = idRepoServiceImpl.getIdRepo(uin);
//			// }
//			// }
//
//			Optional<String> uinNumber = uinRepository
//					.findUinFromUinTableByJoinTableUinAndVid(authRequestDTO.getIdvId());
//			if (uinNumber.isPresent()) {
//				String uin = uinNumber.get();
//				idRepo = idRepoServiceImpl.getIdRepo(uin);
//			}
//		}
//
//		return idRepo;
//	}

}

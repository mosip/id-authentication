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

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.dto.indauth.AuthRequestDTO;
import io.mosip.authentication.core.dto.indauth.AuthResponseDTO;
import io.mosip.authentication.core.dto.indauth.AuthStatusInfo;
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
import io.mosip.authentication.service.impl.id.service.impl.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
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

	/**
	 * Process the authorisation type and authorisation response is returned.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @return the auth response DTO
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 * @throws IdAuthenticationDaoException
	 * @throws ParseException
	 */

	@Override
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO)
			throws IdAuthenticationBusinessException {

		String refId = processIdType(authRequestDTO);
		AuthResponseDTO authResponseDTO;
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance();
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		try {
		idInfo = getIdEntity(refId);

		authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIdvIdType())
				.setReqTime(authRequestDTO.getReqTime()).setVersion(authRequestDTO.getVer());

			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, idInfo, refId);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			authResponseDTO = authResponseBuilder.build();
			logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE,
					"authenticateApplicant status : " + authResponseDTO.getStatus());
			if(idInfo != null) {
				sendAuthNotification(authRequestDTO, refId, authResponseDTO, idInfo);
			}
			auditData();
		}

		return authResponseDTO;

	}

	private void sendAuthNotification(AuthRequestDTO authRequestDTO, String refId, AuthResponseDTO authResponseDTO, Map<String, List<IdentityInfoDTO>> idInfo) throws IdAuthenticationBusinessException {

		boolean ismaskRequired = Boolean.parseBoolean(env.getProperty("uin.masking.required"));

		Map<String, Object> values = new HashMap<>();
		values.put(NAME, demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo).getValue());
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

				Stream.of(AuthType.values()).filter(authType -> authType.isAuthTypeEnabled(authRequestDTO))
						.map(AuthType::getDisplayName).distinct().collect(Collectors.joining(",")));
		if (authResponseDTO.getStatus().equalsIgnoreCase(STATUS_SUCCESS)) {
			values.put(STATUS, "Passed");
		} else {
			values.put(STATUS, "Failed");
		}

		String phoneNumber = null;
		String email = null;
		phoneNumber = demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo).getValue();
		email = demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo).getValue();

		notificationManager.sendNotification(values, email, phoneNumber, SenderType.AUTH);
	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param idInfo 
	 * @param refId
	 *            the ref id
	 * @return the list
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO, Map<String, List<IdentityInfoDTO>> idInfo, String refId)
			throws IdAuthenticationBusinessException {
		List<AuthStatusInfo> authStatusList = new ArrayList<>();
		AuthStatusInfo statusInfo = null;
		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus;
			try {

				otpValidationStatus = otpService.validateOtp(authRequestDTO, refId);
				authStatusList.add(otpValidationStatus);
				statusInfo = otpValidationStatus;
			} finally {
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "OTP Authentication status : " + statusInfo);
			}
			// TODO log authStatus - authType, response

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
			}
			// TODO log authStatus - authType, response

		}
		if (authRequestDTO.getAuthType().isBio()) {
			AuthStatusInfo bioValidationStatus;
			try {

				// TODO log authStatus - authType, response
				bioValidationStatus = bioAuthService.validateBioDetails(authRequestDTO, idInfo,
						refId);
				authStatusList.add(bioValidationStatus);
				statusInfo = bioValidationStatus;
			} finally {
				logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "BioMetric Authentication status :" + statusInfo);
			}
		}

		// TODO Update audit details
		auditData();
		return authStatusList;
	}

	/**
	 * Process the IdType and validates the Idtype and upon validation reference Id
	 * is returned in AuthRequestDTO.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @return the string
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public String processIdType(AuthRequestDTO authRequestDTO) throws IdAuthenticationBusinessException {
		String refId = null;
		String reqType = authRequestDTO.getIdvIdType();
		if (reqType.equals(IdType.UIN.getType())) {
			try {
				refId = idAuthService.validateUIN(authRequestDTO.getIdvId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, e.getErrorCode(), e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_UIN, e);
			}
		} else {
			try {
				refId = idAuthService.validateVID(authRequestDTO.getIdvId());
			} catch (IdValidationFailedException e) {
				logger.error(null, null, null, e.getErrorText());
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_VID, e);
			}
		}

		auditData();
		return refId;
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
	 * @param kycAuthRequestDTO
	 *            is DTO of KycAuthRequestDTO
	 * 
	 */
	@Override
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO, AuthResponseDTO authResponseDTO)
			throws IdAuthenticationBusinessException {
		String refId = processIdType(kycAuthRequestDTO.getAuthRequest());
		String key = "ekyc.mua.accesslevel." + kycAuthRequestDTO.getAuthRequest().getMuaCode();
		Map<String, List<IdentityInfoDTO>> idInfo = getIdEntity(refId);
		KycInfo info = kycService.retrieveKycInfo(refId, KycType.getEkycAuthType(env.getProperty(key)),
				kycAuthRequestDTO.isEPrintReq(), kycAuthRequestDTO.isSecLangReq(), idInfo );
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
	public Map<String, List<IdentityInfoDTO>> getIdEntity(String refId) throws IdAuthenticationBusinessException {

		try {
			return idInfoService.getIdInfo(refId);
		} catch (IdAuthenticationDaoException e) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.SERVER_ERROR, e);
		}

	}

}

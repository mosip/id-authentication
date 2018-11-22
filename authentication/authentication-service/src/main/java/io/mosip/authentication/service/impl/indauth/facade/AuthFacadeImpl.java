/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

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
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.exception.IdValidationFailedException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.core.spi.indauth.facade.AuthFacade;
import io.mosip.authentication.core.spi.indauth.service.DemoAuthService;
import io.mosip.authentication.core.spi.indauth.service.KycService;
import io.mosip.authentication.core.spi.indauth.service.OTPAuthService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.SenderType;
import io.mosip.kernel.core.logger.spi.Logger;

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

	private static final String STATUS_SUCCESS = "y";

	private static final String IDA = "IDA";

	private static final String STATUS = "status";

	private static final String AUTH_TYPE = "authType";

	private static final String NAME = "name";

	private static final String UIN2 = "uin";

	private static final String TIME = "time";

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
	private DemoHelper demoHelper;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/** The Kyc Service */
	@Autowired
	private KycService kycService;

	@Autowired
	private Environment env;

	@Autowired
	private NotificationManager notificationManager;

	@Autowired
	private IdInfoService idInfoService;

	@Autowired
	private DemoAuthService demoAuthService;

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
	public AuthResponseDTO authenticateApplicant(AuthRequestDTO authRequestDTO)
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		String refId = processIdType(authRequestDTO);
		
		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance();
		authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIdvIdType())
				.setReqTime(authRequestDTO.getReqTime()).setVersion(authRequestDTO.getVer());

		AuthResponseDTO authResponseDTO;
		
		try {
			List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, refId);
			authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);
		} finally {
			authResponseDTO = authResponseBuilder.build();
			logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE,
					"authenticateApplicant status : " + authResponseDTO.getStatus());
			sendAuthNotification(authRequestDTO, refId, authResponseDTO);
			auditData();
		}

		return authResponseDTO;

	}

	private void sendAuthNotification(AuthRequestDTO authRequestDTO, String refId, AuthResponseDTO authResponseDTO)
			throws IdAuthenticationDaoException, IdAuthenticationBusinessException {
		
		boolean ismaskRequired = Boolean.parseBoolean(env.getProperty("uin.masking.required"));
		
		Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(refId);
		Map<String, Object> values = new HashMap<>();
		values.put(NAME, demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo).getValue());
		String resTime = authResponseDTO.getResTime();
		
		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
		
		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(authRequestDTO.getReqTime(), isoPattern);
		ZoneId zone = zonedDateTime2.getZone();

		ZonedDateTime dateTimeReq = ZonedDateTime.parse(resTime, isoPattern);
		ZonedDateTime dateTimeConvertedToReqZone = dateTimeReq.withZoneSameInstant(zone);
		String changedDate = dateTimeConvertedToReqZone.format(DateTimeFormatter.ofPattern(env.getProperty("notification.date.format")));
		String changedTime = dateTimeConvertedToReqZone.format(DateTimeFormatter.ofPattern(env.getProperty("notification.time.format")));

		values.put(DATE, changedDate);
		values.put(TIME, changedTime);
		Optional<String> uinOpt = idAuthService.getUIN(refId);
		String uin="";
		
		if(uinOpt.isPresent()) {
			uin=uinOpt.get();
			if(ismaskRequired) {
				uin = MaskUtil.generateMaskValue(uin ,
						Integer.parseInt(env.getProperty("uin.masking.charcount")));
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
	 * @param authRequestDTO the auth request DTO
	 * @param refId          the ref id
	 * @return the list
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO, String refId)
			throws IdAuthenticationBusinessException {
		List<AuthStatusInfo> authStatusList = new ArrayList<>();

		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus = otpService.validateOtp(authRequestDTO, refId);
			authStatusList.add(otpValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE, "OTP Authentication status : " + otpValidationStatus);
		}

		if (authRequestDTO.getAuthType().isPersonalIdentity() || authRequestDTO.getAuthType().isAddress()
				|| authRequestDTO.getAuthType().isFullAddress()) {
			AuthStatusInfo demoValidationStatus = demoAuthService.getDemoStatus(authRequestDTO, refId);
			authStatusList.add(demoValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, IDA, AUTH_FACADE,
					"Demographic Authentication status : " + demoValidationStatus);
		}
		// TODO Update audit details
		auditData();
		return authStatusList;
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

		String resTime = new SimpleDateFormat(env.getProperty(DATETIME_PATTERN)).format(new Date());
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
		String refId = processIdType(kycAuthRequestDTO.getAuthRequest());
		KycInfo info = kycService.retrieveKycInfo(refId, KycType.getEkycAuthType(env.getProperty("ekyc.type")),
				kycAuthRequestDTO.isEPrintReq(), kycAuthRequestDTO.isSecLangReq());
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();

		KycResponseDTO response = new KycResponseDTO();
		response.setAuth(authResponseDTO);
		kycAuthResponseDTO.setResponse(response);
		kycAuthResponseDTO.getResponse().setKyc(info);
		kycAuthResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));

		kycAuthResponseDTO.setStatus(authResponseDTO.getStatus());
		String resTime = new SimpleDateFormat(env.getProperty(DATETIME_PATTERN)).format(new Date());
		kycAuthResponseDTO.setResTime(resTime);
		return kycAuthResponseDTO;
	}

}

/*
 * 
 */
package io.mosip.authentication.service.impl.indauth.facade;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import io.mosip.authentication.service.entity.UinEntity;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.indauth.builder.AuthResponseBuilder;
import io.mosip.authentication.service.impl.indauth.builder.AuthType;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.NotificationType;
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
	Environment env;

	@Autowired
	NotificationManager notificationManager;

	@Autowired
	IdInfoService idInfoService;

	@Autowired
	DemoAuthService demoAuthService;

	@Autowired
	IdAuthServiceImpl idAuthServiceImpl;

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
			throws IdAuthenticationBusinessException, IdAuthenticationDaoException {

		/** Property to get the email Notification type */
		String emailproperty = env.getProperty("notification.email");

		/** Property to get the sms Notification type */
		String smsproperty = env.getProperty("notification.sms");

		String ismaskRequired = env.getProperty("uin.masking.required");

		String refId = processIdType(authRequestDTO);
		List<AuthStatusInfo> authStatusList = processAuthType(authRequestDTO, refId);

		AuthResponseBuilder authResponseBuilder = AuthResponseBuilder.newInstance();
		authResponseBuilder.setTxnID(authRequestDTO.getTxnID()).setIdType(authRequestDTO.getIdvIdType())
				.setReqTime(authRequestDTO.getReqTime()).setVersion(authRequestDTO.getVer());

		authStatusList.forEach(authResponseBuilder::addAuthStatusInfo);

		auditData();
		AuthResponseDTO authResponseDTO = authResponseBuilder.build();
		logger.info(DEFAULT_SESSION_ID, "IDA", AUTH_FACADE,
				"authenticateApplicant status : " + authResponseDTO.getStatus());

		NotificationType emailNotification = NotificationType.valueOf(emailproperty);
		NotificationType smsNotification = NotificationType.valueOf(smsproperty);
		Set<NotificationType> notificationType = new HashSet<>();
		notificationType.add(emailNotification);
		notificationType.add(smsNotification);
		Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(refId);
		Map<String, Object> values = new HashMap();
		values.put("NAME", demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo).getValue());
		String dateTime = authResponseDTO.getResTime();
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		Date date1;
		String changedTime = "";
		String changedDate = "";

		try {
			date1 = formatter.parse(dateTime);
			SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
			SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
			changedTime = time.format(date1);
			changedDate = date.format(date1);
		} catch (ParseException e) {
			logger.error(DEFAULT_SESSION_ID, "IDA", AUTH_FACADE, e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_INPUT_PARAMETER, e);
		}
		values.put("DATE", changedDate);
		values.put("TIME", changedTime);
		Optional<UinEntity> uinEntity = idAuthServiceImpl.getUIN(refId);
		values.put("UIN", Optional.ofNullable(uinEntity.get().getId()));
		values.put("AUTHTYPE",
				Stream.of(AuthType.values()).filter(authType -> authType.isAuthTypeEnabled(authRequestDTO))
						.map(AuthType::getType).collect(Collectors.joining(",")));
		if (authResponseDTO.getStatus().equals("y")) {
			values.put("STATUS", "Success");
		} else {
			values.put("STATUS", "Failed");
		}

		String phoneNumber = null;
		String email = null;

		if (ismaskRequired.equals("true")) {
			phoneNumber = MaskUtil.generateMaskValue(demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo).getValue(),
					Integer.parseInt(env.getProperty("uin.masking.charcount")));
			email = MaskUtil.generateMaskValue(demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo).getValue(),
					Integer.parseInt(env.getProperty("uin.masking.charcount")));
		} else {
			phoneNumber = demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo).getValue();
			email = demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo).getValue();
		}

		String type = SenderType.AUTH.getName();

		notificationManager.sendNotification(notificationType, values, email, phoneNumber, type);
		return authResponseDTO;

	}

	/**
	 * Process the authorisation type and corresponding authorisation service is
	 * called according to authorisation type. reference Id is returned in
	 * AuthRequestDTO.
	 *
	 * @param authRequestDTO
	 *            the auth request DTO
	 * @param refId
	 *            the ref id
	 * @return the list
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	public List<AuthStatusInfo> processAuthType(AuthRequestDTO authRequestDTO, String refId)
			throws IdAuthenticationBusinessException {
		List<AuthStatusInfo> authStatusList = new ArrayList<>();

		if (authRequestDTO.getAuthType().isOtp()) {
			AuthStatusInfo otpValidationStatus = otpService.validateOtp(authRequestDTO, refId);
			authStatusList.add(otpValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, "IDA", AUTH_FACADE, "OTP Authentication status : " + otpValidationStatus);
		}

		if (authRequestDTO.getAuthType().isPersonalIdentity() || authRequestDTO.getAuthType().isAddress()
				|| authRequestDTO.getAuthType().isFullAddress()) {
			AuthStatusInfo demoValidationStatus = demoAuthService.getDemoStatus(authRequestDTO, refId);
			authStatusList.add(demoValidationStatus);
			// TODO log authStatus - authType, response
			logger.info(DEFAULT_SESSION_ID, "IDA", AUTH_FACADE,
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
				logger.error(null, null, null, e.getErrorText());
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

		String s = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
		AuthResponseDTO authResponseTspDto = new AuthResponseDTO();
		authResponseTspDto.setStatus("y");
		authResponseTspDto.setErr(Collections.emptyList());
		authResponseTspDto.setResTime(s);
		authResponseTspDto.setTxnID(authRequestDTO.getTxnID());
		return authResponseTspDto;
	}

	@Override
	public KycAuthResponseDTO processKycAuth(KycAuthRequestDTO kycAuthRequestDTO)
			throws IdAuthenticationBusinessException {
		String refId = processIdType(kycAuthRequestDTO.getAuthRequest());
		KycInfo info = kycService.retrieveKycInfo(refId, KycType.getEkycAuthType(env.getProperty("ekyc.type")),
				kycAuthRequestDTO.isEPrintReq(), kycAuthRequestDTO.isSecLangReq());
		KycAuthResponseDTO kycAuthResponseDTO = new KycAuthResponseDTO();
		kycAuthResponseDTO.getResponse().setKyc(info);
		kycAuthResponseDTO.setTtl(env.getProperty("ekyc.ttl.hours"));
		return kycAuthResponseDTO;
	}

}

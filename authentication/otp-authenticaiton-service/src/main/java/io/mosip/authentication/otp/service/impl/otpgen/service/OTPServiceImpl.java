package io.mosip.authentication.otp.service.impl.otpgen.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.common.entity.AutnTxn;
import io.mosip.authentication.common.helper.IdInfoHelper;
import io.mosip.authentication.common.impl.indauth.match.IdaIdMapping;
import io.mosip.authentication.common.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.common.integration.NotificationManager;
import io.mosip.authentication.common.integration.OTPManager;
import io.mosip.authentication.common.repository.AutnTxnRepository;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.NotificationType;
import io.mosip.authentication.core.dto.otpgen.MaskedResponseDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;

/**
 * Service implementation of OtpTriggerService.
 * 
 * @author Rakesh Roshan
 * @author Dinesh Karuppiah.T
 */
@Service
public class OTPServiceImpl implements OTPService {

	private static final String MOSIP_PRIMARY_LANGUAGE = "mosip.primary-language";

	private static final String OTP_GENERATION_FAILED_DESC = "OTP_GENERATION_FAILED";

	private static final String OTP_GENERATION_FAILED_STATUS = "N";

	private static final String OTP_REQUEST_MAX_COUNT = "otp.request.flooding.max-count";

	private static final String OTP_REQUEST_ADD_MINUTES = "otp.request.flooding.duration";

	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionID";

	private static final String IDA = "IDA";

	/** The id auth service. */
	@Autowired
	private IdAuthService<AutnTxn> idAuthService;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The env. */
	@Autowired
	private Environment env;
	@Autowired
	NotificationManager notificationManager;

	@Autowired
	private IdInfoHelper idInfoHelper;

	@Autowired
	private NotificationService notificationService;

	/** The otp manager. */
	@Autowired
	private OTPManager otpManager;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPServiceImpl.class);

	/**
	 * Generate OTP, store the OTP request details for success/failure. And send OTP
	 * notification by sms(on mobile)/mail(on email-id).
	 *
	 * @param otpRequestDto the otp request dto
	 * @return otpResponseDTO
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	@Override
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto, String partnerId)
			throws IdAuthenticationBusinessException {
		String individualId = otpRequestDto.getIndividualId();
		String requestTime = otpRequestDto.getRequestTime();
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (isOtpFlooded(individualId, requestTime)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			String individualIdType = otpRequestDto.getIndividualIdType();
			Map<String, Object> idResDTO = idAuthService.processIdType(individualIdType, individualId, false);
			String uin = String.valueOf(idResDTO.get("uin"));
			String productid = env.getProperty("application.id");
			String transactionId = otpRequestDto.getTransactionID();
			String otpKey = OTPUtil.generateKey(productid, individualId, transactionId, partnerId);
			Optional<String> otpValue = Optional.ofNullable(otpManager.generateOTP(otpKey));
			if (otpValue.isPresent()) {
				otpResponseDTO.setId(otpRequestDto.getId());
				otpResponseDTO.setErrors(Collections.emptyList());
				otpResponseDTO.setTransactionID(transactionId);
				String responseTime = formatDate(new Date(), env.getProperty(DATETIME_PATTERN));
				otpResponseDTO.setResponseTime(responseTime);
				Map<String, List<IdentityInfoDTO>> idInfo = idAuthService.getIdInfo(idResDTO);
				String email = getEmail(idInfo);
				String phoneNumber = getPhoneNumber(idInfo);
				MaskedResponseDTO maskedResponseDTO = new MaskedResponseDTO();
				List<String> otpChannels = otpRequestDto.getOtpChannel();
				
				for (String channel : otpChannels) {
					processChannel(channel,phoneNumber,email,maskedResponseDTO);
				}
				
				otpResponseDTO.setResponse(maskedResponseDTO);
				AutnTxn authTxn = createAuthTxn(individualId, individualIdType, uin, requestTime, transactionId, "Y",
						"OTP_GENERATED", RequestType.OTP_REQUEST);
				idAuthService.saveAutnTxn(authTxn);
				mosipLogger.info(SESSION_ID, this.getClass().getName(), this.getClass().getName(),
						"generated OTP is: " + otpValue.get());
				notificationService.sendOtpNotification(otpRequestDto, otpValue.get(), uin, email, phoneNumber, idInfo);
			} else {
				AutnTxn authTxn = createAuthTxn(individualId, individualIdType, uin, requestTime, transactionId,
						OTP_GENERATION_FAILED_STATUS, OTP_GENERATION_FAILED_DESC, RequestType.OTP_REQUEST);
				idAuthService.saveAutnTxn(authTxn);
				mosipLogger.error(SESSION_ID, this.getClass().getName(), this.getClass().getName(),
						"OTP Generation failed");
				throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
			}
		}
		return otpResponseDTO;

	}

	private void processChannel(String value,String phone,String email,MaskedResponseDTO maskedResponseDTO) throws IdAuthenticationBusinessException {
		if (isNotNullorEmpty(value)) {
			if (value.equalsIgnoreCase(NotificationType.SMS.getChannel())) {
				maskedResponseDTO.setMaskedMobile(MaskUtil.maskMobile(phone));
			}
			else if (value.equalsIgnoreCase(NotificationType.EMAIL.getChannel())) {
				maskedResponseDTO.setMaskedEmail(MaskUtil.maskEmail(email));
			} else {
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorMessage(),
								IdaIdMapping.EMAIL.name()));
			}
		}else {
			throw new IdAuthenticationBusinessException(
					IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorCode(),
					String.format(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED.getErrorMessage(),
							IdaIdMapping.EMAIL.name()));
		}

	}

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 *
	 * @param otpRequestDto the otp request dto
	 * @return true, if is otp flooded
	 * @throws IdAuthenticationBusinessException
	 */
	private boolean isOtpFlooded(String individualId, String requestTime) throws IdAuthenticationBusinessException {
		boolean isOtpFlooded = false;
		Date requestDateTime;
		LocalDateTime reqTime;
		try {
			requestDateTime = DateUtils.parseToDate(requestTime, env.getProperty(DATETIME_PATTERN));
			reqTime = DateUtils.parseDateToLocalDateTime(requestDateTime);
		} catch (ParseException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
		int addMinutes = Integer.parseInt(env.getProperty(OTP_REQUEST_ADD_MINUTES));
		Date addMinutesInOtpRequestDTime = addMinutes(requestDateTime, -addMinutes);
		LocalDateTime addMinutesInOtpRequestDTimes = DateUtils.parseDateToLocalDateTime(addMinutesInOtpRequestDTime);
		int maxCount = Integer.parseInt(env.getProperty(OTP_REQUEST_MAX_COUNT));
		if (autntxnrepository.countRequestDTime(reqTime, addMinutesInOtpRequestDTimes, individualId) > maxCount) {
			isOtpFlooded = true;
		}
		return isOtpFlooded;
	}

	/**
	 * sets AuthTxn entity values
	 * 
	 * @param idvId
	 * @param idvIdType
	 * @param uin
	 * @param reqTime
	 * @param txnId
	 * @param status
	 * @param comment
	 * @param otpRequest
	 * @return
	 */
	private AutnTxn createAuthTxn(String idvId, String idvIdType, String uin, String reqTime, String txnId,
			String status, String comment, RequestType otpRequest) throws IdAuthenticationBusinessException {
		try {
			AutnTxn autnTxn = new AutnTxn();
			autnTxn.setRefId(idvId);
			autnTxn.setRefIdType(idvIdType);
			String id = createId(uin);
			autnTxn.setId(id); // FIXME
			// TODO check
			autnTxn.setCrBy(IDA);
			autnTxn.setCrDTimes(DateUtils.getUTCCurrentDateTime());
			String strUTCDate = DateUtils.getUTCTimeFromDate(DateUtils.parseToDate(reqTime, env.getProperty(DATETIME_PATTERN)));
			autnTxn.setRequestDTtimes(DateUtils.parseToLocalDateTime(strUTCDate));
			autnTxn.setResponseDTimes(DateUtils.getUTCCurrentDateTime()); // TODO check this
			autnTxn.setAuthTypeCode(otpRequest.getRequestType());
			autnTxn.setRequestTrnId(txnId);
			autnTxn.setStatusCode(status);
			autnTxn.setStatusComment(comment);
			// FIXME
			autnTxn.setLangCode(env.getProperty(MOSIP_PRIMARY_LANGUAGE));
			return autnTxn;
		} catch (ParseException | DateTimeParseException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.UNABLE_TO_PROCESS, e);
		}
	}

	/**
	 * Creates UUID
	 * 
	 * @param uin
	 * @return
	 */
	private String createId(String uin) {
		String currentDate = DateUtils.formatDate(new Date(), env.getProperty("datetime.pattern"));
		String uinAndDate = uin + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, uinAndDate).toString();
	}

	/**
	 * Adds a number of minutes(positive/negative) to a date returning a new Date
	 * object. Add positive, date increase in minutes. Add negative, date reduce in
	 * minutes.
	 *
	 * @param date   the date
	 * @param minute the minute
	 * @return the date
	 */
	private Date addMinutes(Date date, int minute) {
		return DateUtils.addMinutes(date, minute);
	}

	/**
	 * Formate date.
	 *
	 * @param date   the date
	 * @param format the formate
	 * @return the date
	 */
	private String formatDate(Date date, String format) {
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * Get Mail.
	 * 
	 * @param idInfo List of IdentityInfoDTO
	 * @return mail
	 * @throws IdAuthenticationBusinessException
	 */
	private String getEmail(Map<String, List<IdentityInfoDTO>> idInfo) throws IdAuthenticationBusinessException {
		return idInfoHelper.getEntityInfoAsString(DemoMatchType.EMAIL, idInfo);
	}

	/**
	 * Get Mobile number.
	 * 
	 * @param idInfo List of IdentityInfoDTO
	 * @return Mobile number
	 * @throws IdAuthenticationBusinessException
	 */
	private String getPhoneNumber(Map<String, List<IdentityInfoDTO>> idInfo) throws IdAuthenticationBusinessException {
		return idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo);
	}

	private boolean isNotNullorEmpty(String email) {
		return email != null && !email.isEmpty() && email.trim().length() > 0;
	}

}

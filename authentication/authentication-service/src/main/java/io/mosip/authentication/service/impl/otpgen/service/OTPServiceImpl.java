package io.mosip.authentication.service.impl.otpgen.service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.otpgen.MaskedResponseDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.notification.service.NotificationService;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.helper.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.OTPManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.core.exception.ParseException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.UUIDUtils;

/**
 * Service implementation of OtpTriggerService.
 * 
 * @author Rakesh Roshan
 * @author Dineshkaruppiah Thiagarajan
 */
@Service
public class OTPServiceImpl implements OTPService {

	private static final String OTP_REQUEST_MAX_COUNT = "otp.request.max-count";

	private static final String OTP_REQUEST_ADD_MINUTES = "otp.request.add-minutes";

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
	IdRepoService idInfoService;

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
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {
		String otpKey = null;
		String otp = null;
		String mobileNumber = null;
		String email = null;
		String comment = null;
		String status = null;
		String id = otpRequestDto.getId();
		String idvId = otpRequestDto.getIndividualId();
		String idvIdType = otpRequestDto.getIndividualIdType();
		String reqTime = otpRequestDto.getRequestTime();
		String txnId = otpRequestDto.getTransactionID();
		String tspID = otpRequestDto.getPartnerID();
		Map<String, Object> idResDTO = idAuthService.processIdType(idvIdType, idvId, false);
		Map<String, List<IdentityInfoDTO>> idInfo = idAuthService.getIdInfo(idResDTO);
		if (otpRequestDto.getOtpChannel().isPhone()) {
			mobileNumber = getMobileNumber(idInfo);
		}
		if (otpRequestDto.getOtpChannel().isEmail()) {
			email = getEmail(idInfo);
		}

		String uin = String.valueOf(idResDTO.get("uin"));

		if (!checkIsEmptyorNull(email) && otpRequestDto.getOtpChannel().isEmail()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED);
		}

		if (!checkIsEmptyorNull(mobileNumber) && otpRequestDto.getOtpChannel().isPhone()) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.PHONE_EMAIL_NOT_REGISTERED);
		}

		if (isOtpFlooded(otpRequestDto)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			String productid = env.getProperty("application.id");
			otpKey = OTPUtil.generateKey(productid, uin, txnId, tspID);
			try {
				otp = generateOtp(otpKey);
				System.err.println("otpKey >>>" + otpKey);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error(SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			}
		}
		mosipLogger.info(SESSION_ID, this.getClass().getName(), "generated OTP", otp);
		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (otp == null || otp.trim().isEmpty()) {
			status = "N";
			comment = "OTP_GENERATION_FAILED";
			AutnTxn authTxn = createAuthTxn(idvId, idvIdType, uin, reqTime, txnId, status, comment,
					RequestType.OTP_REQUEST);
			idAuthService.saveAutnTxn(authTxn);
			mosipLogger.error(SESSION_ID, this.getClass().getName(), this.getClass().getName(),
					"OTP Generation failed");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
		} else {
			mosipLogger.info(SESSION_ID, this.getClass().getName(), this.getClass().getName(),
					"generated OTP is: " + otp);
			otpResponseDTO.setId(id);
			otpResponseDTO.setErrors(Collections.emptyList());
			otpResponseDTO.setTransactionID(txnId);
			status = "Y";
			comment = "OTP_GENERATED";
			String responseTime = formatDate(new Date(), env.getProperty(DATETIME_PATTERN));
			otpResponseDTO.setResponseTime(responseTime);
			MaskedResponseDTO responseDTO = new MaskedResponseDTO();
			if (checkIsEmptyorNull(email) && otpRequestDto.getOtpChannel().isEmail()) {
				responseDTO.setMaskedEmail(MaskUtil.maskEmail(email));
				otpResponseDTO.setResponse(responseDTO);
			}
			if (checkIsEmptyorNull(mobileNumber) && otpRequestDto.getOtpChannel().isPhone()) {
				responseDTO.setMaskedMobile(MaskUtil.maskMobile(mobileNumber));

			}
			otpResponseDTO.setResponse(responseDTO);
			notificationService.sendOtpNotification(otpRequestDto, otp, uin, email, mobileNumber, idInfo);
			AutnTxn authTxn = createAuthTxn(idvId, idvIdType, uin, reqTime, txnId, status, comment,
					RequestType.OTP_REQUEST);
			idAuthService.saveAutnTxn(authTxn);
		}
		return otpResponseDTO;

	}

	/**
	 * Check is emptyor null.
	 *
	 * @param data the data
	 * @return true, if successful
	 */
	private static boolean checkIsEmptyorNull(String data) {
		return data != null && !data.isEmpty() && data.trim().length() > 0;
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
			String strUTCDate = idInfoHelper.getUTCTime(reqTime);
			autnTxn.setRequestDTtimes(DateUtils.parseToLocalDateTime(strUTCDate));
			autnTxn.setResponseDTimes(DateUtils.getUTCCurrentDateTime()); // TODO check this
			autnTxn.setAuthTypeCode(otpRequest.getRequestType());
			autnTxn.setRequestTrnId(txnId);
			autnTxn.setStatusCode(status);
			autnTxn.setStatusComment(comment);
			// FIXME
			autnTxn.setLangCode(env.getProperty("mosip.primary.lang-code"));
			return autnTxn;
		} catch (ParseException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP,
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
		String currentDate = DateUtils.formatDate(new Date(), env.getProperty("datetime.pattern"));
		String uinAndDate = uin + "-" + currentDate;
		return UUIDUtils.getUUID(UUIDUtils.NAMESPACE_OID, uinAndDate).toString();
	}

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 *
	 * @param otpRequestDto the otp request dto
	 * @return true, if is otp flooded
	 * @throws IdAuthenticationBusinessException
	 */
	private boolean isOtpFlooded(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {
		boolean isOtpFlooded = false;
		String uniqueID = otpRequestDto.getIndividualId();
		Date requestTime;
		LocalDateTime reqTime;
		try {
			requestTime = DateUtils.parseToDate(otpRequestDto.getRequestTime(), env.getProperty(DATETIME_PATTERN));
			reqTime = DateUtils.parseDateToLocalDateTime(requestTime);
		} catch (ParseException e) {
			mosipLogger.error(SESSION_ID, this.getClass().getName(), e.getClass().getName(), e.getMessage());
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.INVALID_OTP_REQUEST_TIMESTAMP,
					e);
		}
		// TODO make minutes and value configurable
		int addMinutes = Integer.parseInt(env.getProperty(OTP_REQUEST_ADD_MINUTES));
		Date addMinutesInOtpRequestDTime = addMinutes(requestTime, -addMinutes);
		LocalDateTime addMinutesInOtpRequestDTimes = DateUtils.parseDateToLocalDateTime(addMinutesInOtpRequestDTime);
		int maxCount = Integer.parseInt(env.getProperty(OTP_REQUEST_MAX_COUNT));
		if (autntxnrepository.countRequestDTime(reqTime, addMinutesInOtpRequestDTimes, uniqueID) > maxCount) {
			isOtpFlooded = true;
		}

		return isOtpFlooded;
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
	private String getMobileNumber(Map<String, List<IdentityInfoDTO>> idInfo) throws IdAuthenticationBusinessException {
		return idInfoHelper.getEntityInfoAsString(DemoMatchType.PHONE, idInfo);
	}

	/**
	 * Generate otp.
	 *
	 * @param otpKey the otp key
	 * @return the string
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private String generateOtp(String otpKey) throws IdAuthenticationBusinessException {
		String otp = null;

		if (otpKey == null || otpKey.trim().isEmpty()) {
			return null;

		} else {
			otp = otpManager.generateOTP(otpKey);

			if (otp == null || otp.trim().isEmpty()) {
				mosipLogger.error(SESSION_ID, this.getClass().getName(), " generateOtp", " generated OTP is: " + otp);
				throw new IdAuthenticationBusinessException(
						IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorCode(),
						String.format(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED.getErrorMessage()));
			}

			mosipLogger.info(SESSION_ID, this.getClass().getName(), " generateOtp", " generated OTP is: " + otp);
		}

		return otp;
	}
}

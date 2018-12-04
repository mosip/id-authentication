package io.mosip.authentication.service.impl.otpgen.facade;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.SenderType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdRepoService;
import io.mosip.authentication.core.spi.otpgen.facade.OTPFacade;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.MaskUtil;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Facade implementation of OTPfacade to generate OTP.
 * 
 * @author Rakesh Roshan
 */
@Service
public class OTPFacadeImpl implements OTPFacade {

	private static final String DATETIME_PATTERN = "datetime.pattern";

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionID";

	private static final String TIME = "time";

	private static final String DATE = "date";

	/** The otp service. */
	@Autowired
	private OTPService otpService;

	/** The id auth service. */
	@Autowired
	private IdAuthService idAuthService;

	/** The autntxnrepository. */
	@Autowired
	private AutnTxnRepository autntxnrepository;

	/** The env. */
	@Autowired
	private Environment env;

	@Autowired
	private DateHelper dateHelper;

	@Autowired
	NotificationManager notificationManager;

	@Autowired
	private IdInfoHelper demoHelper;

	@Autowired
	IdRepoService idInfoService;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPFacadeImpl.class);

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
		String date = null;
		String time = null;

		String refId = getRefId(otpRequestDto);
		String productid = env.getProperty("application.id");
		String txnID = otpRequestDto.getTxnID();

		if (isOtpFlooded(otpRequestDto)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
			try {
				otp = otpService.generateOtp(otpKey);
				String[] dateAndTime = getDateAndTime(otpRequestDto.getReqTime());
				date = dateAndTime[0];
				time = dateAndTime[1];
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error("", otpRequestDto.getIdvIdType(), e.getErrorCode(), "Error: " + e);
			}
		}
		mosipLogger.info(SESSION_ID, "NA", "generated OTP", otp);

		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (otp == null || otp.trim().isEmpty()) {
			status = "N";
			comment = "OTP_GENERATION_FAILED";
			saveAutnTxn(otpRequestDto, status, comment, refId);
			mosipLogger.error("SessionId", "NA", "NA", "OTP Generation failed");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
		} else {
			mosipLogger.info("NA", "NA", "NA", "generated OTP is: " + otp);
			otpResponseDTO.setStatus("Y");
			otpResponseDTO.setTxnId(txnID);
			status = "Y";
			comment = "OTP_GENERATED";
			mobileNumber = getMobileNumber(refId);
			email = getEmail(refId);

			String responseTime = formatDate(new Date(), env.getProperty(DATETIME_PATTERN));
			otpResponseDTO.setResTime(responseTime);
			otpResponseDTO.setMaskedEmail(MaskUtil.maskEmail(email));
			otpResponseDTO.setMaskedMobile(MaskUtil.maskMobile(mobileNumber));

			// -- send otp notification --
			sendOtpNotification(otpRequestDto, otp, refId, date, time, email, mobileNumber);
			saveAutnTxn(otpRequestDto, status, comment, refId);

		}
		return otpResponseDTO;

	}

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 *
	 * @param otpRequestDto the otp request dto
	 * @return true, if is otp flooded
	 * @throws IDDataValidationException
	 * @throws IdAuthenticationBusinessException
	 */
	private boolean isOtpFlooded(OtpRequestDTO otpRequestDto) throws IDDataValidationException {
		boolean isOtpFlooded = false;
		String uniqueID = otpRequestDto.getIdvId();
		Date requestTime = dateHelper.convertStringToDate(otpRequestDto.getReqTime());
		Date addMinutesInOtpRequestDTime = addMinutes(requestTime, -1);

		if (autntxnrepository.countRequestDTime(requestTime, addMinutesInOtpRequestDTime, uniqueID) > 3) {
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
	 * Save the input Request to trigger OTP.
	 *
	 * @param otpRequestDto the otp request dto
	 * @param refId
	 * @throws IDDataValidationException
	 */
	private void saveAutnTxn(OtpRequestDTO otpRequestDto, String status, String comment, String refId)
			throws IDDataValidationException {
		String txnID = otpRequestDto.getTxnID();

		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setRefId(refId);
		autnTxn.setRefIdType(otpRequestDto.getIdvIdType());

		autnTxn.setId(String.valueOf(new Date().getTime())); // FIXME

		// TODO check
		autnTxn.setCrBy("OTP Generate Service");
		autnTxn.setCrDTimes(new Date());
		// FIXME utilize Instant
		autnTxn.setRequestDTtimes(dateHelper.convertStringToDate(otpRequestDto.getReqTime()));
		autnTxn.setResponseDTimes(new Date()); // TODO check this
		autnTxn.setAuthTypeCode(RequestType.OTP_REQUEST.getRequestType());
		autnTxn.setRequestTrnId(txnID);
		autnTxn.setStatusCode(status);
		autnTxn.setStatusComment(comment);
		// FIXME
		autnTxn.setLangCode(env.getProperty("mosip.primary.lang-code"));

		autntxnrepository.saveAndFlush(autnTxn);
	}

	/**
	 * Obtain the reference id for IDType.
	 *
	 * @param otpRequestDto the otp request dto
	 * @return the ref id
	 * @throws IdAuthenticationBusinessException the id authentication business
	 *                                           exception
	 */
	private String getRefId(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {
		String refId = null;
		Optional<IdType> idType = IdType.getIDType(otpRequestDto.getIdvIdType());
		String uniqueID = otpRequestDto.getIdvId();
		if (idType.isPresent()) {
			if (idType.get() == IdType.UIN) {
				refId = idAuthService.validateUIN(uniqueID);
				if (refId == null) {
					mosipLogger.info(SESSION_ID, "IDTYPE-" + otpRequestDto.getIdvIdType(), "Reference Id for UID",
							" UID-refId: " + refId);
				}
			} else {
				refId = idAuthService.validateVID(uniqueID);

				if (refId == null) {
					mosipLogger.info(SESSION_ID, "IDTYPE-" + otpRequestDto.getIdvIdType(), "Reference Id for VID",
							" VID-refId: " + refId);
				}
			}
			mosipLogger.info("NA", idType.get().getType(), "NA",
					" reference id of ID Type " + idType.get().getType() + refId);
		}

		return refId;
	}

	private void sendOtpNotification(OtpRequestDTO otpRequestDto, String otp, String refId, String date, String time,
			String email, String mobileNumber) {

		String maskedUin = null;
		Map<String, Object> values = new HashMap<>();
		try {
			Optional<String> uinOpt = idAuthService.getUIN(refId);
			if (uinOpt.isPresent()) {
				String uin = uinOpt.get();
				maskedUin = MaskUtil.generateMaskValue(uin, Integer.parseInt(env.getProperty("uin.masking.charcount")));
			}
			values.put("uin", maskedUin);
			values.put("otp", otp);
			values.put("validTime", env.getProperty("otp.expiring.time"));
			values.put(DATE, date);
			values.put(TIME, time);

			Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(refId);
			values.put("name", demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo));

			notificationManager.sendNotification(values, email, mobileNumber, SenderType.OTP,
					env.getProperty("otp.notification.type"));
		} catch (BaseCheckedException e) {
			mosipLogger.error(SESSION_ID, "send OTP notification to : ", email, "and " + mobileNumber);
		}
	}

	private String getEmail(String refId) {
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String email = null;
		try {
			idInfo = idInfoService.getIdInfo(refId);
			email = demoHelper.getEntityInfo(DemoMatchType.EMAIL, idInfo);
		} catch (IdAuthenticationDaoException e) {
			mosipLogger.error(SESSION_ID, " email id : ", email, "and ");
		}
		return email;
	}

	private String getMobileNumber(String refId) {
		Map<String, List<IdentityInfoDTO>> idInfo = null;
		String mobileNumber = null;
		try {
			idInfo = idInfoService.getIdInfo(refId);
			mobileNumber = demoHelper.getEntityInfo(DemoMatchType.PHONE, idInfo);
		} catch (IdAuthenticationDaoException e) {
			mosipLogger.error(SESSION_ID, " mobile number id : ", mobileNumber, "and ");
		}
		return mobileNumber;
	}

	private String[] getDateAndTime(String reqquestTime) {

		String[] dateAndTime = new String[2];

		DateTimeFormatter isoPattern = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));

		ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(reqquestTime, isoPattern);
		ZoneId zone = zonedDateTime2.getZone();
		ZonedDateTime dateTime3 = ZonedDateTime.now(zone);
		ZonedDateTime dateTime = dateTime3.withZoneSameInstant(zone);
		String date = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
		dateAndTime[0] = date;
		String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		dateAndTime[1] = time;

		return dateAndTime;

	}
}

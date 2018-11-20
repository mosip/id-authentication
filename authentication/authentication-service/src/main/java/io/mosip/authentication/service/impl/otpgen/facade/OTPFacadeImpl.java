package io.mosip.authentication.service.impl.otpgen.facade;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.core.spi.otpgen.facade.OTPFacade;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoMatchType;
import io.mosip.authentication.service.integration.NotificationManager;
import io.mosip.authentication.service.integration.SenderType;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.authentication.service.repository.DemoRepository;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.StringUtils;

/**
 * Facade implementation of OTPfacade to generate OTP.
 * 
 * @author Rakesh Roshan
 */
@Service
public class OTPFacadeImpl implements OTPFacade {

	/** The Constant SESSION_ID. */
	private static final String SESSION_ID = "SessionID";

	/** The otp service. */
	@Autowired
	private OTPService otpService;

	/** The demo repository. */
	@Autowired
	private DemoRepository demoRepository;

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
	private DemoHelper demoHelper;

	@Autowired
	IdInfoService idInfoService;

	/** The mosip logger. */
	private static Logger mosipLogger = IdaLogger.getLogger(OTPFacadeImpl.class);

	/**
	 * Obtained OTP.
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

		String refId = getRefId(otpRequestDto);
		String productid = env.getProperty("application.id");
		String txnID = otpRequestDto.getTxnID();
		Date otpGenerateTime = null;

		if (isOtpFlooded(otpRequestDto)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
			otpGenerateTime = new Date();
			try {
				otp = otpService.generateOtp(otpKey);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error("", otpRequestDto.getIdvIdType(), e.getErrorCode(), "Error: " + e);
			}
		}
		mosipLogger.info(SESSION_ID, "NA", "generated OTP", otp);

		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (otp == null || otp.trim().isEmpty()) {
			mosipLogger.error("SessionId", "NA", "NA", "OTP Generation failed");
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
		} else {
			mosipLogger.info("NA", "NA", "NA", "generated OTP is: " + otp);
//			Optional<DemoEntity> demoEntity = demoRepository.findById(refId);
			otpResponseDTO.setStatus("Y");
			otpResponseDTO.setTxnId(txnID);
			
			String dateString = formatDate(new Date(), env.getProperty("datetime.pattern"));
			otpResponseDTO.setResTime(dateString);

//			if (demoEntity.isPresent()) {
//				otpResponseDTO.setMaskedEmail(maskEmail(demoEntity.get().getEmail()));
//				otpResponseDTO.setMaskedMobile(maskMobile(demoEntity.get().getMobile()));
//
//				// send otp notification
//				String otpGenerationTime = formatDate(otpGenerateTime, env.getProperty("datetime.pattern"));
//				sendOtpNotification(otpRequestDto, otp, refId, otpGenerationTime, demoEntity.get().getEmail(),
//						demoEntity.get().getMobile());
//
//			}
			saveAutnTxn(otpRequestDto, refId);

		}
		return otpResponseDTO;
	}

	/**
	 * Mask email.
	 *
	 * @param email the email
	 * @return the string
	 */
	private String maskEmail(String email) {
		StringBuilder maskedEmail = new StringBuilder(email);
		IntStream.range(1, StringUtils.split(email, '@')[0].length() + 1).filter(i -> i % 3 != 0)
				.forEach(i -> maskedEmail.setCharAt(i - 1, 'X'));
		return maskedEmail.toString();
	}

	/**
	 * Mask mobile number.
	 *
	 * @param email the email
	 * @return the string
	 */
	private String maskMobile(String mobileNumber) {
		StringBuilder maskedMobile = new StringBuilder(mobileNumber);
		IntStream.range(0, (maskedMobile.length() / 2) + 1).forEach(i -> maskedMobile.setCharAt(i, 'X'));
		return maskedMobile.toString();
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
	private void saveAutnTxn(OtpRequestDTO otpRequestDto, String refId) throws IDDataValidationException {
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
		autnTxn.setStatusCode("Y"); // FIXME
		autnTxn.setStatusComment("OTP_GENERATED"); // FIXME
		//FIXME
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

	private void sendOtpNotification(OtpRequestDTO otpRequestDto, String otp, String refId, String otpGenerationTime,
			String email, String mobileNumber) {

		Map<String, Object> values = new HashMap();
		try {
			values.put("uin", otpRequestDto.getIdvId());
			values.put("otp", otp);
			values.put("validTime", env.getProperty("otp.expiring.time"));
			values.put("datetimestamp", otpGenerationTime);
			Map<String, List<IdentityInfoDTO>> idInfo = idInfoService.getIdInfo(refId);
			values.put("name", demoHelper.getEntityInfo(DemoMatchType.NAME_PRI, idInfo).getValue());

			notificationManager.sendNotification(values, email, mobileNumber, SenderType.OTP);
		} catch (BaseCheckedException e) {
			mosipLogger.error(SESSION_ID, "send OTP notification to : ", email, "and " + mobileNumber);
		}
	}
}

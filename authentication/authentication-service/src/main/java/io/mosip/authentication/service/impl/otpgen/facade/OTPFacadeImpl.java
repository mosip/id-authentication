package io.mosip.authentication.service.impl.otpgen.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IDDataValidationException;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.idauth.service.IdAuthService;
import io.mosip.authentication.core.spi.otpgen.facade.OTPFacade;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.helper.DateHelper;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoEntity;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.authentication.service.repository.DemoRepository;
import io.mosip.kernel.core.spi.logger.MosipLogger;
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

	/** The mosip logger. */
	private static MosipLogger mosipLogger = IdaLogger.getLogger(OTPFacadeImpl.class);

	/**
	 * Obtained OTP.
	 *
	 * @param otpRequestDto
	 *            the otp request dto
	 * @return otpResponseDTO
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
	 */
	@Override
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {
		String otpKey = null;
		String otp = null;

		String refId = getRefId(otpRequestDto);
		String productid = env.getProperty("application.id");
		String txnID = otpRequestDto.getTxnID();

		if (isOtpFlooded(otpRequestDto)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		} else {
			otpKey = OTPUtil.generateKey(productid, refId, txnID, otpRequestDto.getMuaCode());
			try {
				otp = otpService.generateOtp(otpKey);
			} catch (IdAuthenticationBusinessException e) {
				mosipLogger.error("", otpRequestDto.getIdvIdType(), e.getErrorCode(), "Error: " + e);
			}
		}
		mosipLogger.info(SESSION_ID, "NA", "generated OTP", otp);

		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (otp == null || otp.trim().isEmpty()) {
			mosipLogger.error("SessionId", "NA", "NA", "generated OTP is: " + otp);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
		} else {
			mosipLogger.info("NA", "NA", "NA", "generated OTP is: " + otp);
			Optional<DemoEntity> demoEntity = demoRepository.findById(refId);
			otpResponseDTO.setStatus("Y");
			otpResponseDTO.setTxnID(txnID);
			if (demoEntity.isPresent()) {
				otpResponseDTO.setResponseTime(formateDate(new Date(), env.getProperty("datetime.pattern")));
				otpResponseDTO.setMaskedEmail(maskEmail(demoEntity.get().getEmail()));
				otpResponseDTO.setMaskedMobile(maskMobile(demoEntity.get().getMobile()));
			}
			saveAutnTxn(otpRequestDto);
		}
		return otpResponseDTO;
	}

	/**
	 * Mask email.
	 *
	 * @param email
	 *            the email
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
	 * @param email
	 *            the email
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
	 * @param otpRequestDto
	 *            the otp request dto
	 * @return true, if is otp flooded
	 * @throws IDDataValidationException 
	 * @throws IdAuthenticationBusinessException 
	 */
	private boolean isOtpFlooded(OtpRequestDTO otpRequestDto) throws IDDataValidationException {
		boolean isOtpFlooded = false;
			String uniqueID = otpRequestDto.getIdvId();
			Date requestTime = dateHelper.convertStringToDate(otpRequestDto.getReqTime());
			System.err.println(requestTime);
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
	 * @param date
	 *            the date
	 * @param minute
	 *            the minute
	 * @return the date
	 */
	private Date addMinutes(Date date, int minute) {
		return DateUtils.addMinutes(date, minute);
	}

	/**
	 * Formate date.
	 *
	 * @param date
	 *            the date
	 * @param formate
	 *            the formate
	 * @return the date
	 */
	private Date formateDate(Date date, String formate) {
		Date formatedDate = new Date();
		String formatDate = DateUtils.formatDate(date, formate);
		try {
			formatedDate = new SimpleDateFormat(formate).parse(formatDate);
			return formatedDate;
		} catch (ParseException e) {
			mosipLogger.error(SESSION_ID, "ParseException", e.getMessage(), "Date formate parse Exception");
		}
		return formatedDate;
	}

	/**
	 * Save the input Request to trigger OTP.
	 *
	 * @param otpRequestDto
	 *            the otp request dto
	 * @throws IDDataValidationException 
	 */
	private void saveAutnTxn(OtpRequestDTO otpRequestDto) throws IDDataValidationException {
		String uniqueID = otpRequestDto.getIdvId();
		String txnID = otpRequestDto.getTxnID();

		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setUin(uniqueID);
		autnTxn.setId(String.valueOf(new Date().getTime())); // FIXME

		// TODO check
		autnTxn.setCrBy("OTP Generate Service");
		autnTxn.setCrDTimes(new Date());
		// FIXME utilize Instant
		autnTxn.setRequestDTtimes(dateHelper.convertStringToDate(otpRequestDto.getReqTime()));
		autnTxn.setResponseDTimes(new Date()); // TODO check this
		autnTxn.setAuthTypeCode(RequestType.OTP_REQUEST.getRequestType());
		autnTxn.setRequestTxnId(txnID);
		autnTxn.setIsActive("y");
		autnTxn.setStatusCode("OTP_GENERATED"); // FIXME
		autnTxn.setStatusComment("OTP_GENERATED"); // FIXME

		autntxnrepository.saveAndFlush(autnTxn);
	}

	/**
	 * Obtain the reference id for IDType.
	 *
	 * @param otpRequestDto
	 *            the otp request dto
	 * @return the ref id
	 * @throws IdAuthenticationBusinessException
	 *             the id authentication business exception
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

}

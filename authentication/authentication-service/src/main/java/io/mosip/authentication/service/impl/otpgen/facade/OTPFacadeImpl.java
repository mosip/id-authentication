package io.mosip.authentication.service.impl.otpgen.facade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.constant.RequestType;
import io.mosip.authentication.core.dto.indauth.IdType;
import io.mosip.authentication.core.dto.otpgen.OtpRequestDTO;
import io.mosip.authentication.core.dto.otpgen.OtpResponseDTO;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.logger.IdaLogger;
import io.mosip.authentication.core.spi.idauth.service.IdAuthService;
import io.mosip.authentication.core.spi.otpgen.facade.OTPFacade;
import io.mosip.authentication.core.spi.otpgen.service.OTPService;
import io.mosip.authentication.core.util.OTPUtil;
import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.authentication.service.repository.AutnTxnRepository;
import io.mosip.kernel.core.spi.logger.MosipLogger;
import io.mosip.kernel.core.util.DateUtils;

/**
 * Facade implementation of OTPfacade to generate OTP.
 * 
 * @author Rakesh Roshan
 */
@Service
@PropertySource("classpath:application-local.properties")
public class OTPFacadeImpl implements OTPFacade {

	private static final String SESSION_ID = "SessionID";

	@Autowired
	private OTPService otpService;

	@Autowired
	private IdAuthService idAuthService;

	@Autowired
	private AutnTxnRepository autntxnrepository;

	@Autowired
	private Environment env;

	private static MosipLogger mosipLogger = IdaLogger.getLogger(OTPFacadeImpl.class);

	/**
	 * Obtained OTP.
	 * 
	 * @param otpRequestDto
	 * @return otpResponseDTO
	 * @throws IdAuthenticationBusinessException
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
				mosipLogger.error("", otpRequestDto.getIdType(), e.getErrorCode(), "Error: " + e);
			}
		}
		mosipLogger.info(SESSION_ID, "NA", "generated OTP", otp);

		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		if (otp == null || otp.trim().isEmpty()) {
			mosipLogger.error("SessionId", "NA", "NA", "generated OTP is: " + otp);
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_GENERATION_FAILED);
		} else {
			mosipLogger.info("NA", "NA", "NA", "generated OTP is: " + otp);
			otpResponseDTO.setStatus("Y");
			otpResponseDTO.setTxnID(txnID);
			otpResponseDTO.setResponseTime(formateDate(new Date(), env.getProperty("datetime.pattern")));
			// TODO Date format to be included
			saveAutnTxn(otpRequestDto);
		}
		return otpResponseDTO;
	}

	/**
	 * Validate the number of request for OTP generation. Limit for the number of
	 * request for OTP is should not exceed 3 in 60sec.
	 * 
	 * @param otpRequestDto
	 * @return
	 */
	private boolean isOtpFlooded(OtpRequestDTO otpRequestDto) {
		boolean isOtpFlooded = false;
		String uniqueID = otpRequestDto.getId();
		Date requestTime = otpRequestDto.getReqTime();
		Date addMinutesInOtpRequestDTime = addMinites(requestTime, -1);

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
	 * @param minute
	 * @return
	 */
	private Date addMinites(Date date, int minute) {
		return DateUtils.addMinutes(date, minute);
	}

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
	 */
	private void saveAutnTxn(OtpRequestDTO otpRequestDto) {
		String uniqueID = otpRequestDto.getId();
		String txnID = otpRequestDto.getTxnID();

		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setUin(uniqueID);
		autnTxn.setId(String.valueOf(new Date().getTime())); // FIXME

		// TODO check
		autnTxn.setCrBy("OTP Generate Service");
		autnTxn.setCrDTimes(new Date());
		autnTxn.setRequestDTtimes(otpRequestDto.getReqTime());
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
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	private String getRefId(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {
		String refId = null;
		Optional<IdType> idType = IdType.getIDType(otpRequestDto.getIdType());
		String uniqueID = otpRequestDto.getId();
		if (idType.isPresent()) {
			if (idType.get().equals(IdType.UIN)) {
				refId = idAuthService.validateUIN(uniqueID);
				if (refId == null) {
					mosipLogger.info(SESSION_ID, "IDTYPE-" + otpRequestDto.getIdType(), "Reference Id for UID",
							" UID-refId: " + refId);
				}
			} else {
				refId = idAuthService.validateVID(uniqueID);

				if (refId == null) {
					mosipLogger.info(SESSION_ID, "IDTYPE-" + otpRequestDto.getIdType(), "Reference Id for VID",
							" VID-refId: " + refId);
				}
			}
			mosipLogger.info("NA", idType.get().getType(), "NA",
					" reference id of ID Type " + idType.get().getType() + refId);
		}

		return refId;
	}

}

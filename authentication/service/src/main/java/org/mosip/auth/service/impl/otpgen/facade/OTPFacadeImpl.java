package org.mosip.auth.service.impl.otpgen.facade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mosip.auth.core.constant.IdAuthenticationErrorConstants;
import org.mosip.auth.core.dto.indauth.AuthError;
import org.mosip.auth.core.dto.indauth.IDType;
import org.mosip.auth.core.dto.otpgen.OtpRequestDTO;
import org.mosip.auth.core.dto.otpgen.OtpResponseDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.spi.idauth.service.IdAuthService;
import org.mosip.auth.core.spi.otpgen.facade.OTPFacade;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.core.util.OTPUtil;
import org.mosip.auth.service.dao.AutnTxnRepository;
import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.mosip.kernel.core.utils.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Facade implementation of OTPfacade to generate OTP.
 * 
 * @author Rakesh Roshan
 */
@Service
@PropertySource("classpath:application-local.properties")
public class OTPFacadeImpl implements OTPFacade {

	@Autowired
	OTPService otpService;

	@Autowired
	IdAuthService idAuthService;

	@Autowired
	AutnTxnRepository autntxnrepository;

	@Autowired
	private Environment env;

	private MosipLogger LOGGER;

	@Autowired
	private void initializeLogger(MosipRollingFileAppender idaRollingFileAppender) {
		LOGGER = MosipLogfactory.getMosipDefaultRollingFileLogger(idaRollingFileAppender, this.getClass());

	}

	/**
	 * 
	 * @param otpKey
	 * @return
	 * @throws IdAuthenticationBusinessException
	 */
	@Override
	public OtpResponseDTO generateOtp(OtpRequestDTO otpRequestDto) throws IdAuthenticationBusinessException {

		OtpResponseDTO otpResponseDTO = new OtpResponseDTO();
		String otpKey = null;
		String otp = null;

		List<AuthError> authErrors = new ArrayList<AuthError>();
		AuthError authError = new AuthError();
		authError.setErrorCode(IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorCode());
		authError.setErrorMessage(IdAuthenticationErrorConstants.OTP_GENERATION_REQUEST_FAILED.getErrorMessage());
		authErrors.add(authError);

		IDType idType = otpRequestDto.getIdType();
		String refId = getRefId(otpRequestDto);
		String productid = env.getProperty("application.id");
		String txnID = otpRequestDto.getTxnID();
		String auaCode = otpRequestDto.getAuaCode();

		if (isOtpFlooded(otpRequestDto)) {
			throw new IdAuthenticationBusinessException(IdAuthenticationErrorConstants.OTP_REQUEST_FLOODED);
		}

		otpKey = OTPUtil.generateKey(productid, refId, txnID, auaCode);

		try {
			otp = otpService.generateOtp(otpKey);
		} catch (IdAuthenticationBusinessException e) {
			LOGGER.error("", idType.getType(), e.getErrorCode(), e.getErrorText());
		}
		if (otp == null || otp.trim().isEmpty()) {
			LOGGER.error("NA", "NA", "NA", "generated OTP is: " + otp);
			otpResponseDTO.setStatus("OTP_GENERATED_FAILED");
			otpResponseDTO.setErrorCode(authErrors);
			otpResponseDTO.setTxnID(txnID);
			otpResponseDTO.setResponseTime(new Date());
		} else {
			LOGGER.info("NA", "NA", "NA", "generated OTP is: " + otp);
			otpResponseDTO.setStatus("OTP_GENERATED");
			otpResponseDTO.setTxnID(txnID);
			otpResponseDTO.setResponseTime(new Date());
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
		String uniqueID = otpRequestDto.getUniqueID();
		Date requestTime = otpRequestDto.getRequestTime();
		String pattern = env.getProperty("date.format.pattern");
		String formatedRequestDTime = formateDate(requestTime, pattern);
		Date addMinutesInOtpRequestDTime = addMinites(requestTime, -1);
		String oneMinBeforeRequestDTime = formateDate(addMinutesInOtpRequestDTime, pattern);

		if (autntxnrepository.countRequestDTime(formatedRequestDTime, oneMinBeforeRequestDTime, uniqueID) > 3) {
			isOtpFlooded = true;
		}
		return isOtpFlooded;
	}

	/**
	 * Convert Date in ISO date and time format. ISO
	 * format:yyyy-MM-dd'T'HH:mm:ss.SSSZ
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	private String formateDate(Date date, String pattern) {
		return DateUtil.formatDate(date, pattern);
	}

	private Date addMinites(Date date, int minute) {
		return DateUtil.addMinutes(date, minute);
	}

	/**
	 * Save the input Request to trigger OTP.
	 * 
	 * @param otpRequestDto
	 */
	private void saveAutnTxn(OtpRequestDTO otpRequestDto) {
		String uniqueID = otpRequestDto.getUniqueID();
		String txnID = otpRequestDto.getTxnID();
		IDType idType = otpRequestDto.getIdType();
		// String pattern = env.getProperty("date.format.pattern");
		// String requestDate = formateDate(otpRequestDto.getRequestTime(), pattern);

		AutnTxn autnTxn = new AutnTxn();
		autnTxn.setUin(uniqueID);
		autnTxn.setId(txnID); // TODO check this

		// TODO check
		autnTxn.setCrBy("OTP Generate Service");
		autnTxn.setCrDTimes(new Date());
		autnTxn.setRequestDTtimes(otpRequestDto.getRequestTime());
		autnTxn.setResponseDTimes(new Date()); // TODO check this
		autnTxn.setAuthTypeCode(idType.getType());
		autnTxn.setRequestTxnId(txnID);
		autnTxn.setIsActive("y");
		autnTxn.setStatusCode("OTP_GENERATED"); // TODO
		autnTxn.setStatusComment("OTP_GENERATED"); // TODO

		autntxnrepository.saveAndFlush(autnTxn);
	}

	/**
	 * Obtain the reference id for IDType.
	 * 
	 * @param otpRequestDto
	 * @return
	 */
	private String getRefId(OtpRequestDTO otpRequestDto) {
		String refId = null;
		IDType idType = otpRequestDto.getIdType();
		String uniqueID = otpRequestDto.getUniqueID();
		try {
			if (idType.equals(IDType.UIN)) {
				refId = idAuthService.validateUIN(uniqueID);
			} else if (otpRequestDto.getIdType().equals(IDType.VID)) {
				refId = idAuthService.validateVID(uniqueID);
			}
			LOGGER.info("NA", idType.getType(), "NA", " reference id of ID Type " + idType.getType() + refId);
		} catch (IdAuthenticationBusinessException e) {
			LOGGER.error("", idType.getType(), e.getErrorCode(), e.getErrorText());
		}
		return refId;
	}

}

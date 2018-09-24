package org.mosip.auth.service.impl.otpgen.facade;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.mosip.auth.core.dto.indauth.IDType;
import org.mosip.auth.core.dto.indauth.OtpRequestDTO;
import org.mosip.auth.core.exception.IdAuthenticationBusinessException;
import org.mosip.auth.core.exception.IdValidationFailedException;
import org.mosip.auth.core.spi.idauth.service.IdAuthService;
import org.mosip.auth.core.spi.otpgen.facade.OTPFacade;
import org.mosip.auth.core.spi.otpgen.service.OTPService;
import org.mosip.auth.core.util.OTPUtil;
import org.mosip.auth.service.dao.AutnTxnRepository;
import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.kernel.core.logging.MosipLogger;
import org.mosip.kernel.core.logging.appenders.MosipRollingFileAppender;
import org.mosip.kernel.core.logging.factory.MosipLogfactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * Facade implementation of OTPfacade.
 * 
 * @author Rakesh Roshan
 */
@Service
@PropertySource("classpath:audit.properties")
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
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.auth.core.spi.indauth.facade.OTPfacade#generateOtp(org.mosip.auth.
	 * core.dto.indauth.OtpRequestDTO)
	 */

	@Override
	public boolean generateOtp(OtpRequestDTO otpRequestDto) {

		boolean isOtpGenerated = true;
		String otpKey = null;
		String otp = null;
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

//		String productId = env.getProperty("productid");
		String productid = "IDA";
		String txnID = otpRequestDto.getTxnID();
		String auaCode = otpRequestDto.getAuaCode();
		
		// -- generate OTP key
		otpKey = OTPUtil.generateKey(productid, refId, txnID, auaCode);

		// -- generate OTP
		try {
			otp = otpService.generateOtp(otpKey);
		} catch (IdAuthenticationBusinessException e) {
			LOGGER.error("", idType.getType(), e.getErrorCode(), e.getErrorText());
			e.printStackTrace();
		}

		if (otp == null || otp.trim().isEmpty()) {
			LOGGER.error("NA", "NA", "NA", "generated OTP is: " + otp);
			isOtpGenerated = false;
		} else {
			LOGGER.info("NA", "NA", "NA", "generated OTP is: " + otp);
			
			
			AutnTxn autnTxn = new AutnTxn();
			autnTxn.setUin(uniqueID);
			autnTxn.setId(txnID); //TODO check this
			
			//TODO check
			autnTxn.setCrBy("OTP Generate Service");
			autnTxn.setCrDTimes(new Date());
			autnTxn.setRequestDTtimes(otpRequestDto.getRequestTime());
			autnTxn.setResponseDTimes(new Date()); //TODO check this
			autnTxn.setAuthTypeCode(idType.getType());
			autnTxn.setRequestTxnId(txnID);
			autnTxn.setIsActive("y");
			autnTxn.setStatusCode("OTP_GENERATED"); // TODO
			autnTxn.setStatusComment("OTP_GENERATED"); // TODO
			
			autntxnrepository.saveAndFlush(autnTxn);
		}

		return isOtpGenerated;
	}

	

}

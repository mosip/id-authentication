package org.mosip.kernel.otpmanagerservice.service.impl;

import java.time.LocalDateTime;
import java.util.ResourceBundle;

import org.mosip.kernel.core.spi.otpmanager.OtpGenerator;
import org.mosip.kernel.otpmanagerapi.generator.OtpGeneratorImpl;
import org.mosip.kernel.otpmanagerservice.constant.OtpExpiryConstants;
import org.mosip.kernel.otpmanagerservice.constant.OtpStatusConstants;
import org.mosip.kernel.otpmanagerservice.dto.OtpGeneratorRequestDto;
import org.mosip.kernel.otpmanagerservice.dto.OtpGeneratorResponseDto;
import org.mosip.kernel.otpmanagerservice.entity.OtpEntity;
import org.mosip.kernel.otpmanagerservice.repository.OtpRepository;
import org.mosip.kernel.otpmanagerservice.service.OtpGeneratorService;
import org.mosip.kernel.otpmanagerservice.util.OtpManagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides the implementation for the methods of OtpGeneratorService
 * interface.
 * 
 * @author Sagar Mahapatra
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class OtpGeneratorServiceImpl implements OtpGeneratorService {
	/**
	 * The reference that autowires OtpRepository class.
	 */
	@Autowired
	private OtpRepository otpRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.otpmanagerservice.service.OtpGeneratorService#getOtp(org.
	 * mosip.kernel.otpmanagerservice.dto.OtpGeneratorRequestDto)
	 */
	public OtpGeneratorResponseDto getOtp(OtpGeneratorRequestDto otpDto) {
		// Creating object of OtpGenerator class present in API, that generates OTP.
		OtpGenerator otpGenerator = new OtpGeneratorImpl();
		// Creating object to return the generation response.
		OtpGeneratorResponseDto response = new OtpGeneratorResponseDto();
		// Checking whether the key exists in the repository.
		OtpEntity keyCheck = otpRepository.findById(OtpEntity.class, otpDto.getKey());

		/*
		 * Creating object of ResourceBundle to read the constant values from properties
		 * file.
		 */
		ResourceBundle resource = ResourceBundle
				.getBundle(OtpExpiryConstants.OTP_PROPERTIES_FILE_NAME.getStringProperty());
		int keyFreezeDuration = Integer
				.parseInt(resource.getString(OtpExpiryConstants.USER_FREEZE_DURATION.getStringProperty()));
		/*
		 * This condition checks whether the key requesting the OTP generation is
		 * freezed or not, else the OTP is generated.
		 */
		if ((keyCheck != null) && (keyCheck.getOtpStatus().equals(OtpStatusConstants.KEY_FREEZED.getProperty()))
				&& (OtpManagerUtils.timeDifferenceInSeconds(keyCheck.getValidationTime(),
						LocalDateTime.now()) <= keyFreezeDuration)) {
			response.setOtp(OtpStatusConstants.SET_AS_NULL_IN_STRING.getProperty());
			response.setStatus(OtpStatusConstants.BLOCKED_USER.getProperty());
		} else {
			String generatedOtp = otpGenerator.generateOtp();
			OtpEntity otp = new OtpEntity();
			otp.setKeyId(otpDto.getKey());
			otp.setNumOfAttempt(OtpExpiryConstants.DEFAULT_NUM_OF_ATTEMPT.getProperty());
			otp.setGeneratedOtp(generatedOtp);
			otpRepository.save(otp);
			response.setOtp(generatedOtp);
			response.setStatus(OtpStatusConstants.GENERATION_SUCCESSFUL.getProperty());
		}
		return response;
	}
}

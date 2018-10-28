package io.mosip.kernel.otpmanager.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;
import io.mosip.kernel.otpmanager.constant.OtpExpiryConstants;
import io.mosip.kernel.otpmanager.constant.OtpStatusConstants;
import io.mosip.kernel.otpmanager.dto.OtpGeneratorRequestDto;
import io.mosip.kernel.otpmanager.dto.OtpGeneratorResponseDto;
import io.mosip.kernel.otpmanager.entity.OtpEntity;
import io.mosip.kernel.otpmanager.exception.MosipErrors;
import io.mosip.kernel.otpmanager.exception.MosipResourceNotFoundExceptionHandler;
import io.mosip.kernel.otpmanager.impl.OtpGeneratorImpl;
import io.mosip.kernel.otpmanager.repository.OtpRepository;
import io.mosip.kernel.otpmanager.service.OtpGeneratorService;
import io.mosip.kernel.otpmanager.util.OtpManagerUtils;

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
	 * io.mosip.kernel.otpmanagerservice.service.OtpGeneratorService#getOtp(org.
	 * mosip.kernel.otpmanagerservice.dto.OtpGeneratorRequestDto)
	 */
	public OtpGeneratorResponseDto getOtp(OtpGeneratorRequestDto otpDto) {
		// Creating object of OtpGenerator class present in API, that generates OTP.
		OtpGeneratorImpl otpGenerator = new OtpGeneratorImpl();
		// Creating object to return the generation response.
		OtpGeneratorResponseDto response = new OtpGeneratorResponseDto();
		// Checking whether the key exists in the repository.
		OtpEntity keyCheck = otpRepository.findById(OtpEntity.class, otpDto.getKey());

		String generatedOtp;
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
			try {
				generatedOtp = otpGenerator.generateOtp();
			} catch (MissingResourceException exception) {
				List<MosipErrors> validationErrorsList = new ArrayList<>();
				validationErrorsList.add(new MosipErrors(OtpErrorConstants.OTP_GEN_RESOURCE_NOT_FOUND.getErrorCode(),
						OtpErrorConstants.OTP_GEN_RESOURCE_NOT_FOUND.getErrorMessage()));
				throw new MosipResourceNotFoundExceptionHandler(validationErrorsList);
			}
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

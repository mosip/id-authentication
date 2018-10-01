package org.mosip.kernel.otpmanagerservice.service.impl;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.mosip.kernel.otpmanagerservice.constant.OtpErrorConstants;
import org.mosip.kernel.otpmanagerservice.constant.OtpExpiryConstants;
import org.mosip.kernel.otpmanagerservice.constant.OtpStatusConstants;
import org.mosip.kernel.otpmanagerservice.constant.SqlQueryConstants;
import org.mosip.kernel.otpmanagerservice.entity.OtpEntity;
import org.mosip.kernel.otpmanagerservice.exceptionhandler.MosipErrors;
import org.mosip.kernel.otpmanagerservice.exceptionhandler.MosipOtpInvalidArgumentExceptionHandler;
import org.mosip.kernel.otpmanagerservice.repository.OtpRepository;
import org.mosip.kernel.otpmanagerservice.service.OtpValidatorService;
import org.mosip.kernel.otpmanagerservice.util.OtpManagerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class provides the implementation for the methods of OtpGeneratorService
 * interface.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Service
public class OtpValidatorServiceImpl implements OtpValidatorService {
	/**
	 * The reference that autowires OtpRepository.
	 */
	@Autowired
	OtpRepository otpRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.otpmanagerservice.service.OtpValidatorService#validateOtp(
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public boolean validateOtp(String key, String otp) {
		// This method validates the input parameters.
		OtpManagerUtils.validateOtpRequestArguments(key, otp);

		// The OTP entity for a specific key.
		OtpEntity otpResponse = otpRepository.findById(OtpEntity.class, key);

		/*
		 * Checking whether the key exists in repository or not. If not, throw an
		 * exception.
		 */
		if (otpResponse == null) {
			List<MosipErrors> validationErrorsList = new ArrayList<>();
			validationErrorsList.add(new MosipErrors(OtpErrorConstants.OTP_VAL_KEY_NOT_FOUND.getErrorCode(),
					OtpErrorConstants.OTP_VAL_KEY_NOT_FOUND.getErrorMessage()));
			throw new MosipOtpInvalidArgumentExceptionHandler(validationErrorsList);

		}

		// This variable holds the entity class name, required to update the data.
		String otpEntityClassName = OtpEntity.class.getSimpleName();

		// This variable holds the update query to be performed.
		String updateString;

		// This variable holds the result of OTP validation.
		boolean isValidationSuccess = false;

		// This variable holds the count of number
		int attemptCount = otpResponse.getNumOfAttempt();

		// Creating ResourceBundle object to read data from properties file.
		ResourceBundle resource = ResourceBundle
				.getBundle(OtpExpiryConstants.OTP_PROPERTIES_FILE_NAME.getStringProperty());

		// This variable holds the number of validation attempts allowed.
		int numberOfValidationAttemptsAllowed = Integer
				.parseInt(resource.getString(OtpExpiryConstants.ALLOWED_NUMBER_OF_ATTEMPTS.getStringProperty()));

		// This condition increases the validation attempt count.
		if ((attemptCount < numberOfValidationAttemptsAllowed)
				&& (otpResponse.getOtpStatus().equals(OtpStatusConstants.UNUSED_OTP.getProperty()))) {
			updateString = SqlQueryConstants.UPDATE.getProperty() + " " + otpEntityClassName
					+ " SET num_of_attempt = :newNumOfAttempt,"
					+ "validation_time = :newValidationTime WHERE key_id=:id";
			HashMap<String, Object> updateMap = createUpdateMap(key, null, attemptCount + 1, LocalDateTime.now());
			updateData(updateString, updateMap);
		}

		/*
		 * This condition freezes the key for a certain time, if the validation attempt
		 * reaches the maximum allowed limit.
		 */
		if (attemptCount == numberOfValidationAttemptsAllowed) {
			updateString = SqlQueryConstants.UPDATE.getProperty() + " " + otpEntityClassName
					+ " SET otp_status = :newOtpStatus," + "validation_time = :newValidationTime,"
					+ "num_of_attempt = :newNumOfAttempt WHERE key_id=:id";
			HashMap<String, Object> updateMap = createUpdateMap(key, OtpStatusConstants.KEY_FREEZED.getProperty(),
					Integer.valueOf(OtpExpiryConstants.DEFAULT_NUM_OF_ATTEMPT.getProperty()),
					OtpManagerUtils.getCurrentLocalDateTime());
			updateData(updateString, updateMap);

		}

		/*
		 * This condition un-freezes the key, after the pre-defined freeze period is
		 * crossed.
		 */
		if ((otpResponse.getOtpStatus().equals(OtpStatusConstants.KEY_FREEZED.getProperty())
				&& ((OtpManagerUtils.timeDifferenceInSeconds(otpResponse.getValidationTime(),
						OtpManagerUtils.getCurrentLocalDateTime())) > (Integer.parseInt(
								resource.getString(OtpExpiryConstants.USER_FREEZE_DURATION.getStringProperty())))))) {
			updateString = SqlQueryConstants.UPDATE.getProperty() + " " + otpEntityClassName
					+ " SET otp_status = :newOtpStatus," + " num_of_attempt = :newNumOfAttempt,"
					+ " validation_time = :newValidationTime WHERE key_id=:id";
			HashMap<String, Object> updateMap = createUpdateMap(key, OtpStatusConstants.UNUSED_OTP.getProperty(),
					Integer.valueOf(attemptCount + 1), OtpManagerUtils.getCurrentLocalDateTime());
			if (otp.equals(otpResponse.getGeneratedOtp())) {
				isValidationSuccess = true;
				otpRepository.deleteById(key);
			} else {
				updateData(updateString, updateMap);
			}
		}

		/*
		 * This condition validates the OTP if neither the key is in freezed condition,
		 * nor the OTP has expired. If the OTP validation is successful, the entire
		 * record is deleted.
		 */
		if ((otpResponse.getGeneratedOtp().equals(otp))
				&& (otpResponse.getOtpStatus().equals(OtpStatusConstants.UNUSED_OTP.getProperty()))
				&& ((OtpManagerUtils.timeDifferenceInSeconds(otpResponse.getGenerationTime(),
						OtpManagerUtils.getCurrentLocalDateTime())) <= (Integer.parseInt(
								resource.getString(OtpExpiryConstants.OTP_EXPIRY_TIME_LIMIT.getStringProperty()))))) {
			otpRepository.deleteById(key);
			isValidationSuccess = true;
		}
		return isValidationSuccess;
	}

	// This method creates UPDATE map required for UPDATE operation.
	HashMap<String, Object> createUpdateMap(String key, String status, Integer newNumberOfAttempt,
			LocalDateTime localDateTime) {
		HashMap<String, Object> updateMap = new HashMap<>();
		if (key != null) {
			updateMap.put(SqlQueryConstants.ID.getProperty(), key);
		}
		if (status != null) {
			updateMap.put(SqlQueryConstants.NEW_OTP_STATUS.getProperty(), status);
		}
		if (newNumberOfAttempt != null) {
			updateMap.put(SqlQueryConstants.NEW_NUM_OF_ATTEMPT.getProperty(), newNumberOfAttempt);
		}
		if (localDateTime != null) {
			updateMap.put(SqlQueryConstants.NEW_VALIDATION_TIME.getProperty(), localDateTime);
		}
		return updateMap;
	}

	// This method handles UPDATE query operations.
	void updateData(String updateString, HashMap<String, Object> updateMap) {
		otpRepository.createQueryUpdateOrDelete(updateString, updateMap);
	}
}
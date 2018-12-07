package io.mosip.kernel.otpmanager.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.otpmanager.spi.OtpValidator;
import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;
import io.mosip.kernel.otpmanager.constant.OtpStatusConstants;
import io.mosip.kernel.otpmanager.constant.SqlQueryConstants;
import io.mosip.kernel.otpmanager.dto.OtpValidatorResponseDto;
import io.mosip.kernel.otpmanager.entity.OtpEntity;
import io.mosip.kernel.otpmanager.exception.RequiredKeyNotFoundException;
import io.mosip.kernel.otpmanager.repository.OtpRepository;
import io.mosip.kernel.otpmanager.util.OtpManagerUtils;

/**
 * This class provides the implementation for the methods of OtpValidatorService
 * interface.
 * 
 * @author Sagar Mahapatra
 * @since 1.0.0
 *
 */
@Service
public class OtpValidatorServiceImpl implements OtpValidator<ResponseEntity<OtpValidatorResponseDto>> {
	/**
	 * The reference that autowires OtpRepository.
	 */
	@Autowired
	OtpRepository otpRepository;

	/**
	 * The reference that autowires OtpManagerUtils.
	 */
	@Autowired
	OtpManagerUtils otpUtils;

	@Value("${mosip.kernel.otp.validation-attempt-threshold}")
	String numberOfValidationAttemptsAllowed;

	@Value("${mosip.kernel.otp.key-freeze-time}")
	String keyFreezeDuration;

	@Value("${mosip.kernel.otp.expiry-time}")
	String otpExpiryLimit;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.otpmanager.service.OtpValidatorService#validateOtp(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public ResponseEntity<OtpValidatorResponseDto> validateOtp(String key, String otp) {
		// This method validates the input parameters.
		otpUtils.validateOtpRequestArguments(key, otp);
		OtpValidatorResponseDto responseDto;
		ResponseEntity<OtpValidatorResponseDto> validationResponseEntity;
		// The OTP entity for a specific key.
		OtpEntity otpResponse = otpRepository.findById(OtpEntity.class, key);
		responseDto = new OtpValidatorResponseDto();
		responseDto.setMessage(OtpStatusConstants.FAILURE_MESSAGE.getProperty());
		responseDto.setStatus(OtpStatusConstants.FAILURE_STATUS.getProperty());
		validationResponseEntity = new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);

		/*
		 * Checking whether the key exists in repository or not. If not, throw an
		 * exception.
		 */
		if (otpResponse == null) {
			List<ServiceError> validationErrorsList = new ArrayList<>();
			validationErrorsList.add(new ServiceError(OtpErrorConstants.OTP_VAL_KEY_NOT_FOUND.getErrorCode(),
					OtpErrorConstants.OTP_VAL_KEY_NOT_FOUND.getErrorMessage()));
			throw new RequiredKeyNotFoundException(validationErrorsList);
		}
		// This variable holds the update query to be performed.
		String updateString;
		// This variable holds the count of number
		int attemptCount = otpResponse.getValidationRetryCount();
		if ((OtpManagerUtils.timeDifferenceInSeconds(otpResponse.getGeneratedDtimes(),
				OtpManagerUtils.getCurrentLocalDateTime())) > (Integer.parseInt(otpExpiryLimit))) {

			responseDto.setStatus(OtpStatusConstants.FAILURE_STATUS.getProperty());
			responseDto.setMessage(OtpStatusConstants.OTP_EXPIRED_STATUS.getProperty());
			return new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
		}
		// This condition increases the validation attempt count.
		if ((attemptCount < Integer.parseInt(numberOfValidationAttemptsAllowed))
				&& (otpResponse.getStatusCode().equals(OtpStatusConstants.UNUSED_OTP.getProperty()))) {
			updateString = SqlQueryConstants.UPDATE.getProperty() + " " + OtpEntity.class.getSimpleName()
					+ " SET validation_retry_count = :newNumOfAttempt,"
					+ "upd_dtimes = :newValidationTime WHERE id=:id";
			HashMap<String, Object> updateMap = createUpdateMap(key, null, attemptCount + 1, LocalDateTime.now());
			updateData(updateString, updateMap);
		}
		/*
		 * This condition freezes the key for a certain time, if the validation attempt
		 * reaches the maximum allowed limit.
		 */
		if (attemptCount == Integer.parseInt(numberOfValidationAttemptsAllowed)) {
			updateString = SqlQueryConstants.UPDATE.getProperty() + " " + OtpEntity.class.getSimpleName()
					+ " SET status_code = :newOtpStatus," + "upd_dtimes = :newValidationTime,"
					+ "validation_retry_count = :newNumOfAttempt WHERE id=:id";
			HashMap<String, Object> updateMap = createUpdateMap(key, OtpStatusConstants.KEY_FREEZED.getProperty(), 0,
					OtpManagerUtils.getCurrentLocalDateTime());
			updateData(updateString, updateMap);
			responseDto.setStatus(OtpStatusConstants.FAILURE_STATUS.getProperty());
			responseDto.setMessage(OtpStatusConstants.FAILURE_AND_FREEZED_MESSAGE.getProperty());
			validationResponseEntity = new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
			return validationResponseEntity;

		}
		validationResponseEntity = unFreezeKey(key, otp, otpResponse, attemptCount, responseDto,
				validationResponseEntity);
		/*
		 * This condition validates the OTP if neither the key is in freezed condition,
		 * nor the OTP has expired. If the OTP validation is successful the specific
		 * message is returned as response and the entire record is deleted. If the OTP
		 * is expired, the specific message is returned as response and the entire
		 * record is deleted.
		 */
		if ((otpResponse.getOtp().equals(otp))
				&& (otpResponse.getStatusCode().equals(OtpStatusConstants.UNUSED_OTP.getProperty())
						&& ((OtpManagerUtils.timeDifferenceInSeconds(otpResponse.getGeneratedDtimes(),
								OtpManagerUtils.getCurrentLocalDateTime())) <= (Integer.parseInt(otpExpiryLimit))))) {
			responseDto.setStatus(OtpStatusConstants.SUCCESS_STATUS.getProperty());
			responseDto.setMessage(OtpStatusConstants.SUCCESS_MESSAGE.getProperty());
			otpRepository.deleteById(key);
			return new ResponseEntity<>(responseDto, HttpStatus.OK);
		}
		return validationResponseEntity;
	}

	/**
	 * This method handles the freeze conditions i.e., If the key is freezed, it
	 * blocks the validation for the assigned freeze period. If the key is freezed
	 * and has completed the freeze time, it unfreezes the key.
	 * 
	 * @param key
	 *            the key.
	 * @param otp
	 *            the OTP.
	 * @param otpResponse
	 *            the OTP response.
	 * @param attemptCount
	 *            the attempt count.
	 * @param responseDto
	 *            the response dto.
	 * @param validationResponseEntity
	 *            the validation response entity.
	 * @return the response entity.
	 */
	private ResponseEntity<OtpValidatorResponseDto> unFreezeKey(String key, String otp, OtpEntity otpResponse,
			int attemptCount, OtpValidatorResponseDto responseDto,
			ResponseEntity<OtpValidatorResponseDto> validationResponseEntity) {
		String updateString;
		if (otpResponse.getStatusCode().equals(OtpStatusConstants.KEY_FREEZED.getProperty())) {
			if ((OtpManagerUtils.timeDifferenceInSeconds(otpResponse.getUpdatedDtimes(),
					OtpManagerUtils.getCurrentLocalDateTime())) > (Integer.parseInt(keyFreezeDuration))) {
				updateString = SqlQueryConstants.UPDATE.getProperty() + " " + OtpEntity.class.getSimpleName()
						+ " SET status_code = :newOtpStatus," + " validation_retry_count = :newNumOfAttempt,"
						+ " upd_dtimes = :newValidationTime WHERE id=:id";
				HashMap<String, Object> updateMap = createUpdateMap(key, OtpStatusConstants.UNUSED_OTP.getProperty(),
						Integer.valueOf(attemptCount + 1), OtpManagerUtils.getCurrentLocalDateTime());
				if (otp.equals(otpResponse.getOtp())) {
					responseDto.setStatus(OtpStatusConstants.SUCCESS_STATUS.getProperty());
					responseDto.setMessage(OtpStatusConstants.SUCCESS_MESSAGE.getProperty());
					validationResponseEntity = new ResponseEntity<>(responseDto, HttpStatus.OK);
					otpRepository.deleteById(key);
				} else {
					updateData(updateString, updateMap);
				}
			} else {
				responseDto.setMessage(OtpStatusConstants.FAILURE_AND_FREEZED_MESSAGE.getProperty());
				validationResponseEntity = new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
			}
		}
		return validationResponseEntity;
	}

	/**
	 * This method creates the UPDATE map required for UPDATE operations.
	 * 
	 * @param key
	 *            the key to be updated.
	 * @param status
	 *            the status to be updated.
	 * @param newNumberOfAttempt
	 *            the new number of attempt value.
	 * @param localDateTime
	 *            the new LocalDateTime.
	 * @return the map.
	 */
	private HashMap<String, Object> createUpdateMap(String key, String status, Integer newNumberOfAttempt,
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

	/**
	 * This method handles UPDATE query operations.
	 * 
	 * @param updateString
	 *            the query string.
	 * @param updateMap
	 *            the query map.
	 */
	private void updateData(String updateString, HashMap<String, Object> updateMap) {
		otpRepository.createQueryUpdateOrDelete(updateString, updateMap);
	}
}
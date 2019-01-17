package io.mosip.kernel.idgenerator.rid.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.idgenerator.rid.constant.RidGeneratorExceptionConstant;
import io.mosip.kernel.idgenerator.rid.constant.RidGeneratorPropertyConstant;
import io.mosip.kernel.idgenerator.rid.entity.Rid;
import io.mosip.kernel.idgenerator.rid.exception.EmptyInputException;
import io.mosip.kernel.idgenerator.rid.exception.InputLengthException;
import io.mosip.kernel.idgenerator.rid.exception.NullValueException;
import io.mosip.kernel.idgenerator.rid.exception.RidException;
import io.mosip.kernel.idgenerator.rid.repository.RidRepository;

/**
 * This class generate 28 digits registration id.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Component
public class RidGeneratorImpl implements RidGenerator<String> {

	@Value("${mosip.kernel.rid.centerid-length:-1}")
	private int centerIdLength;

	@Value("${mosip.kernel.rid.machineid-length:-1}")
	private int machineIdLength;

	@Value("${mosip.kernel.rid.sequence-max-digit}")
	private int sequenceMaxDigit;

	@Value("${mosip.kernel.rid.sequence-initial-value:1}")
	private int sequenceInitialValue;

	private String sequenceFormat;
	private int sequenceEndvalue;

	@Autowired
	RidRepository ridRepository;

	@PostConstruct
	public void constructValue() {
		sequenceFormat = "%0" + sequenceMaxDigit + "d";
		sequenceEndvalue = MathUtils.getPow(10, sequenceMaxDigit) - 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.RidGenerator#generateId(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public String generateId(String centreId, String machineId) {
		validateInput(centreId, machineId, centerIdLength, machineIdLength);

		String randomDigitRid = sequenceNumberGenerator();

		return appendString(randomDigitRid, getcurrentTimeStamp(), centreId, machineId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.RidGenerator#generateId(java.lang.
	 * String, java.lang.String, int, int)
	 */
	@Override
	public String generateId(String centreId, String machineId, int centerIdLength, int machineIdLength) {
		validateInput(centreId, machineId, centerIdLength, machineIdLength);

		String randomDigitRid = sequenceNumberGenerator();

		return appendString(randomDigitRid, getcurrentTimeStamp(), centreId, machineId);
	}

	/**
	 * This method is used to validate the input given by user
	 * 
	 * @param centreId
	 *            input by user
	 * @param machineId
	 *            input by user
	 */
	private void validateInput(String centreId, String machineId, int centerIdLength, int machineIdLength) {

		if (centerIdLength <= 0 || machineIdLength <= 0) {
			throw new InputLengthException(
					RidGeneratorExceptionConstant.CENTERIDLENGTH_AND_MACHINEIDLENGTH_VALUE_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.CENTERIDLENGTH_AND_MACHINEIDLENGTH_VALUE_ERROR_CODE
							.getErrorMessage());
		}

		if (centreId == null || machineId == null) {

			throw new NullValueException(RidGeneratorExceptionConstant.NULL_VALUE_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.NULL_VALUE_ERROR_CODE.getErrorMessage());
		}
		if (centreId.isEmpty() || machineId.isEmpty()) {

			throw new EmptyInputException(RidGeneratorExceptionConstant.EMPTY_INPUT_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.EMPTY_INPUT_ERROR_CODE.getErrorMessage());
		}
		if (centreId.length() != centerIdLength || machineId.length() != machineIdLength) {

			throw new InputLengthException(RidGeneratorExceptionConstant.INPUT_LENGTH_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.INPUT_LENGTH_ERROR_CODE.getErrorMessage());
		}

	}

	/**
	 * This method generates a five digit sequence number.
	 * 
	 * @return generated five digit random number
	 */
	private String sequenceNumberGenerator() {
		Rid entity = null;
		try {
			entity = ridRepository.findLastRid();
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new RidException(RidGeneratorExceptionConstant.RID_FETCH_EXCEPTION.getErrorCode(),
					RidGeneratorExceptionConstant.RID_FETCH_EXCEPTION.errorMessage, e);
		}
		try {
			if (entity == null || entity.getCurrentSequenceNo() == sequenceEndvalue) {
				entity = new Rid();
				entity.setCurrentSequenceNo(sequenceInitialValue);
				ridRepository.save(entity);
			} else {
				ridRepository.updateRid(entity.getCurrentSequenceNo() + 1, entity.getCurrentSequenceNo());
			}
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new RidException(RidGeneratorExceptionConstant.RID_UPDATE_EXCEPTION.getErrorCode(),
					RidGeneratorExceptionConstant.RID_UPDATE_EXCEPTION.errorMessage, e);
		}
		return String.format(sequenceFormat, entity.getCurrentSequenceNo());
	}

	/**
	 * This method appends the different strings to generate the RID
	 * 
	 * @param randomDigitRid
	 *            5 digit no. generated
	 * @param currentTimeStamp
	 *            current timestamp generated
	 * @return generated RID
	 */
	private String appendString(String randomDigitRid, String currentTimeStamp, String centreId, String dongleId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(centreId).append(dongleId).append(randomDigitRid).append(currentTimeStamp);
		return (stringBuilder.toString().trim());
	}

	/**
	 * This method gets the current timestamp in yyyymmddhhmmss format.
	 * 
	 * @return current timestamp in fourteen digits
	 */
	private String getcurrentTimeStamp() {
		DateTimeFormatter format = DateTimeFormatter
				.ofPattern(RidGeneratorPropertyConstant.TIMESTAMP_FORMAT.getProperty());
		return LocalDateTime.now().format(format);
	}

}

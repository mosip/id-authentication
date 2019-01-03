package io.mosip.kernel.ridgenerator.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorExceptionConstant;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorPropertyConstant;
import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.exception.EmptyInputException;
import io.mosip.kernel.ridgenerator.exception.InputLengthException;
import io.mosip.kernel.ridgenerator.exception.NullValueException;
import io.mosip.kernel.ridgenerator.repository.RidRepository;

/**
 * This class generate 28 digits registration id.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Component
public class RidGeneratorImpl implements RidGenerator<String> {

	@Value("${mosip.kernel.rid.centerid.length}")
	private int centerIdLength;

	@Value("${mosip.kernel.rid.dongleid.length}")
	private int machineIdLength;

	@Autowired
	RidRepository ridRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idgenerator.spi.RidGenerator#generateId(java.lang.
	 * String, java.lang.String)
	 */
	@Override
	public String generateId(String centreId, String machineId) {
		validateInput(centreId, machineId, centerIdLength, machineIdLength);

		String randomDigitRid = sequenceNumberGenerator(machineId);

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

		String randomDigitRid = sequenceNumberGenerator(machineId);

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
	 * This method generates a five digit number against dongleId provided.
	 * 
	 * @return generated five digit random number
	 */
	private String sequenceNumberGenerator(String machineId) {

		final int initialValue = Integer.parseInt(RidGeneratorPropertyConstant.SEQUENCE_START_VALUE.getProperty());

		Rid entity = ridRepository.findById(Rid.class, machineId);

		if (entity == null || entity.getSequenceId() == Integer
				.parseInt(RidGeneratorPropertyConstant.SEQUENCE_END_VALUE.getProperty())) {

			entity = new Rid();
			entity.setDongleId(machineId);
			entity.setSequenceId(initialValue);

		} else {

			entity.setSequenceId(entity.getSequenceId() + 1);

		}
		ridRepository.save(entity);
		return String.format("%05d", entity.getSequenceId());
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

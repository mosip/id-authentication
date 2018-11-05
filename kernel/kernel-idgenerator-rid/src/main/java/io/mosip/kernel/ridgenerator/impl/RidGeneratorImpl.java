package io.mosip.kernel.ridgenerator.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idgenerator.RidGenerator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorExceptionConstant;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorPropertyConstant;
import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.exception.MosipEmptyInputException;
import io.mosip.kernel.ridgenerator.exception.MosipInputLengthException;
import io.mosip.kernel.ridgenerator.exception.MosipNullValueException;
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

	@Autowired
	RidRepository repository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.idgenerator.MosipEidGenerator#eidGeneration(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public String generateId(String centreId, String dongleId) {

		validateInput(centreId, dongleId);

		centreId = StringUtils.removeLeftChar(centreId,
				Integer.parseInt(RidGeneratorPropertyConstant.CENTERID_MIN_LENGTH.getProperty()));

		dongleId = StringUtils.removeLeftChar(dongleId,
				Integer.parseInt(RidGeneratorPropertyConstant.DONGLEID_MIN_LENGTH.getProperty()));

		String randomDigitRid = sequenceNumberGenerator(dongleId);

		return appendString(randomDigitRid, getcurrentTimeStamp(), centreId, dongleId);
	}

	/**
	 * This method is used to validate the input given by user
	 * 
	 * @param centreId
	 *            input by user
	 * @param dongleId
	 *            input by user
	 */
	private void validateInput(String centreId, String dongleId) {

		if (centreId == null || dongleId == null) {

			throw new MosipNullValueException(RidGeneratorExceptionConstant.MOSIP_NULL_VALUE_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.MOSIP_NULL_VALUE_ERROR_CODE.getErrorMessage());
		}
		if (centreId.isEmpty() || dongleId.isEmpty()) {

			throw new MosipEmptyInputException(
					RidGeneratorExceptionConstant.MOSIP_EMPTY_INPUT_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.MOSIP_EMPTY_INPUT_ERROR_CODE.getErrorMessage());
		}
		if (centreId.length() < Integer.parseInt(RidGeneratorPropertyConstant.CENTERID_MIN_LENGTH.getProperty())
				|| dongleId.length() < Integer
						.parseInt(RidGeneratorPropertyConstant.DONGLEID_MIN_LENGTH.getProperty())) {

			throw new MosipInputLengthException(
					RidGeneratorExceptionConstant.MOSIP_INPUT_LENGTH_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.MOSIP_INPUT_LENGTH_ERROR_CODE.getErrorMessage());
		}

	}

	/**
	 * This method generates a five digit number against dongleId provided.
	 * 
	 * @return generated five digit random number
	 */
	private String sequenceNumberGenerator(String dongleId) {

		final int initialValue = Integer.parseInt(RidGeneratorPropertyConstant.SEQUENCE_START_VALUE.getProperty());

		Rid entity = repository.findById(Rid.class, dongleId);

		if (entity == null || entity.getSequenceId() == Integer
				.parseInt(RidGeneratorPropertyConstant.SEQUENCE_END_VALUE.getProperty())) {

			entity = new Rid();
			entity.setDongleId(dongleId);
			entity.setSequenceId(initialValue);

		} else {

			entity.setSequenceId(entity.getSequenceId() + 1);

		}
		repository.save(entity);
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

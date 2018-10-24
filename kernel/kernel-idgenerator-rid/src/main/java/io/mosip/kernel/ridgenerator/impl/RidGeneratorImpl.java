package io.mosip.kernel.ridgenerator.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.spi.idgenerator.MosipRidGenerator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorExceptionConstant;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorPropertyConstant;
import io.mosip.kernel.ridgenerator.entity.RidEntity;
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
public class RidGeneratorImpl implements MosipRidGenerator<String> {

	@Autowired
	RidRepository repository;

	/**s
	 * Input from user(should be atleast 4 char)
	 */
	private String centreId;
	/**
	 * Input from user(should be atleast 5 char)
	 */
	private String dongleId;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.spi.idgenerator.MosipEidGenerator#eidGeneration(java.
	 * lang.String, java.lang.String)
	 */
	// @Override
	public String generateId(String centreId, String dongleId) {
		String rid = null;
		checkInput(centreId, dongleId);
		cleanupId(centreId, dongleId);
		String randomDigitEid = sequenceNumberGen();
		String currentTimeStamp = getcurrentTimeStamp();
		rid = appendString(randomDigitEid, currentTimeStamp);
		return rid;
	}

	/**
	 * This method is used to validate the input given by user
	 * 
	 * @param agentId
	 *            input by user
	 * @param machineId
	 *            input by user
	 */
	private void checkInput(String agentId, String machineId) {

		if (agentId == null || machineId == null) {

			throw new MosipNullValueException(RidGeneratorExceptionConstant.MOSIP_NULL_VALUE_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.MOSIP_NULL_VALUE_ERROR_CODE.getErrorMessage());
		}
		if (agentId.isEmpty() || machineId.isEmpty()) {

			throw new MosipEmptyInputException(
					RidGeneratorExceptionConstant.MOSIP_EMPTY_INPUT_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.MOSIP_EMPTY_INPUT_ERROR_CODE.getErrorMessage());
		}
		if (agentId.length() < Integer.parseInt(RidGeneratorPropertyConstant.CENTERID_MIN_LENGTH.getProperty())
				|| machineId.length() < Integer
						.parseInt(RidGeneratorPropertyConstant.DONGLEID_MIN_LENGTH.getProperty())) {

			throw new MosipInputLengthException(
					RidGeneratorExceptionConstant.MOSIP_INPUT_LENGTH_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.MOSIP_INPUT_LENGTH_ERROR_CODE.getErrorMessage());
		}

	}

	/**
	 * This method is used to clean up the input id
	 * 
	 * @param agentId
	 *            input by user
	 * @param machineId
	 *            input by user
	 */
	private void cleanupId(String agentId, String machineId) {
		centreId = StringUtils.removeLeftChar(agentId,
				Integer.parseInt(RidGeneratorPropertyConstant.CENTERID_MIN_LENGTH.getProperty()));
		dongleId = StringUtils.removeLeftChar(machineId,
				Integer.parseInt(RidGeneratorPropertyConstant.DONGLEID_MIN_LENGTH.getProperty()));

	}

	/**
	 * This method generates a five digit number against dongleId provided.
	 * 
	 * @return generated five digit random number
	 */
	private String sequenceNumberGen() {
		final int incrementor = Integer.parseInt(RidGeneratorPropertyConstant.SEQUENCE_START_VALUE.getProperty());
		RidEntity entity = repository.findById(RidEntity.class, dongleId);
		if (entity == null) {
			RidEntity ridEntity = new RidEntity();
			ridEntity.setDongleId(dongleId);
			ridEntity.setSequenceId(incrementor);
			repository.save(ridEntity);

			return String.format("%05d", incrementor);
		} else {
			if (entity.getSequenceId() == Integer
					.parseInt(RidGeneratorPropertyConstant.SEQUENCE_END_VALUE.getProperty())) {
				RidEntity ridEntity = new RidEntity();
				ridEntity.setDongleId(dongleId);
				ridEntity.setSequenceId(incrementor);
				repository.save(ridEntity);
				return String.format("%05d", incrementor);
			}

			int id = entity.getSequenceId() + 1;
			entity.setSequenceId(id);
			repository.save(entity);
			return String.format("%05d", id);

		}
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
	private String appendString(String randomDigitRid, String currentTimeStamp) {
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

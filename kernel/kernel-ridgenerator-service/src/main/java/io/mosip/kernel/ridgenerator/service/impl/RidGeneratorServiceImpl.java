package io.mosip.kernel.ridgenerator.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.MathUtils;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorExceptionConstant;
import io.mosip.kernel.ridgenerator.constant.RidGeneratorPropertyConstant;
import io.mosip.kernel.ridgenerator.dto.RidGeneratorResponseDto;
import io.mosip.kernel.ridgenerator.entity.Rid;
import io.mosip.kernel.ridgenerator.exception.EmptyInputException;
import io.mosip.kernel.ridgenerator.exception.InputLengthException;
import io.mosip.kernel.ridgenerator.exception.RidException;
import io.mosip.kernel.ridgenerator.repository.RidRepository;
import io.mosip.kernel.ridgenerator.service.RidGeneratorService;

/**
 * The service class for RID generator.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Service
public class RidGeneratorServiceImpl implements RidGeneratorService<RidGeneratorResponseDto> {

	/**
	 * The center id length.
	 */
	@Value("${mosip.kernel.registrationcenterid.length:-1}")
	private int centerIdLength;

	/**
	 * The machine id length.
	 */
	@Value("${mosip.kernel.machineid.length:-1}")
	private int machineIdLength;

	/**
	 * The sequence length.
	 */
	@Value("${mosip.kernel.rid.sequence-length:-1}")
	private int sequenceLength;

	/**
	 * The timestamp.
	 */
	@Value("${mosip.kernel.rid.timestamp-length:-1}")
	private int timeStampLength;

	/**
	 * The sequence initial value.
	 */
	@Value("${mosip.kernel.rid.sequence-initial-value:1}")
	private int sequenceInitialValue;

	/**
	 * Reference to {@link RidRepository}.
	 */
	@Autowired
	private RidRepository repository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.ridgenerator.service.RidGeneratorService#generateRid(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public RidGeneratorResponseDto generateRid(String centerId, String machineId) {
		validateInput(centerId, machineId, centerIdLength, machineIdLength);
		String randomDigitRid = sequenceNumberGenerator(centerId, machineId, sequenceLength);
		String rid = appendString(randomDigitRid, getcurrentTimeStamp(), centerId, machineId);
		RidGeneratorResponseDto response = new RidGeneratorResponseDto();
		response.setRid(rid);
		return response;
	}

	/**
	 * This method generate sequence based on center id and machine id combination.
	 * 
	 * @param centerId
	 *            the center id.
	 * @param machineId
	 *            the machine id.
	 * @param sequenceLength
	 *            the sequence length.
	 * @return the sequence.
	 */
	private String sequenceNumberGenerator(String centerId, String machineId, int sequenceLength) {
		int sequenceId = 0;
		Rid entity = null;
		int sequenceEndvalue = MathUtils.getPow(10, sequenceLength) - 1;
		String sequenceFormat = "%0" + sequenceLength + "d";
		try {
			entity = repository.findRid(centerId, machineId);
		} catch (DataAccessException | DataAccessLayerException e) {
			throw new RidException(RidGeneratorExceptionConstant.RID_FETCH_EXCEPTION.getErrorCode(),
					RidGeneratorExceptionConstant.RID_FETCH_EXCEPTION.getErrorMessage(), e);
		}
		try {

			if (entity == null) {
				entity = new Rid();
				sequenceId = sequenceInitialValue;
				entity.setCurrentSequenceNo(sequenceInitialValue);
				entity.setMachineId(machineId);
				entity.setCenterId(centerId);
				entity.setCreatedBy("SYSTEM");
				entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				repository.save(entity);
			} else {
				entity.setUpdatedBy("SYSTEM"); // Can be changed to log in user
				entity.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
				if (entity.getCurrentSequenceNo() == sequenceEndvalue) {
					sequenceId = sequenceInitialValue;
				} else {
					sequenceId = entity.getCurrentSequenceNo() + 1;
				}
				repository.updateRid(sequenceId, centerId, machineId);
			}

		} catch (DataAccessException | DataAccessLayerException e) {
			throw new RidException(RidGeneratorExceptionConstant.RID_UPDATE_EXCEPTION.getErrorCode(),
					RidGeneratorExceptionConstant.RID_UPDATE_EXCEPTION.errorMessage, e);

		}

		return String.format(sequenceFormat, sequenceId);

	}

	/**
	 * This method gets the current timestamp in yyyyMMddHHmmss format.
	 * 
	 * @return current timestamp in fourteen digits
	 */
	private String getcurrentTimeStamp() {
		DateTimeFormatter format = DateTimeFormatter
				.ofPattern(RidGeneratorPropertyConstant.TIMESTAMP_FORMAT.getProperty());
		return LocalDateTime.now(ZoneId.of("UTC")).format(format);
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
	private String appendString(String randomDigitRid, String currentTimeStamp, String centreId, String machineId) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(centreId).append(machineId).append(randomDigitRid).append(currentTimeStamp);
		return (stringBuilder.toString().trim());
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
		if (centreId.isEmpty() || machineId.isEmpty()) {
			throw new EmptyInputException(RidGeneratorExceptionConstant.EMPTY_INPUT_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.EMPTY_INPUT_ERROR_CODE.getErrorMessage());
		}
		if (centreId.length() != centerIdLength || machineId.length() != machineIdLength) {
			throw new InputLengthException(RidGeneratorExceptionConstant.INPUT_LENGTH_ERROR_CODE.getErrorCode(),
					RidGeneratorExceptionConstant.INPUT_LENGTH_ERROR_CODE.getErrorMessage());
		}

	}

}

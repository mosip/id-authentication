package org.mosip.kernel.eidgenerator;

import java.util.Random;

import org.mosip.kernel.core.spi.idgenerator.MosipEidGenerator;
import org.mosip.kernel.core.utils.StringUtil;
import org.mosip.kernel.eidgenerator.constants.EidGeneratorConstants;
import org.mosip.kernel.eidgenerator.exception.MosipEmptyInputException;
import org.mosip.kernel.eidgenerator.exception.MosipInputLengthException;
import org.mosip.kernel.eidgenerator.exception.MosipNullValueException;

/**
 * This class generates Enrollment ID as per defined policy
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public class EidGenerator implements MosipEidGenerator {

	public static final Random randomNumberGenerator = new Random();
	protected String agentEid;
	protected String machineEid;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.spi.idgenerator.MosipEidGenerator#eidGeneration(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public String eidGeneration(String agentId, String machineId) {
		String eid = null;
		checkInput(agentId, machineId);
		cleanupId(agentId, machineId);
		int randomDigitEid = randomNumberGen();
		long currentTimestamp = getCurrTimestamp();
		eid = appendString(randomDigitEid, currentTimestamp);
		return eid;
	}

	/**
	 * This method is used to validate the input given by user
	 * 
	 * @param agentId
	 *            input by user
	 * @param machineId
	 *            input by user
	 */
	public void checkInput(String agentId, String machineId) {

		if (agentId == null || machineId == null) {

			throw new MosipNullValueException(EidGeneratorConstants.MOSIP_NULL_VALUE_ERROR_CODE.getErrorCode(),
					EidGeneratorConstants.MOSIP_NULL_VALUE_ERROR_CODE.getErrorMessage());
		}
		if (agentId.isEmpty() || machineId.isEmpty()) {

			throw new MosipEmptyInputException(EidGeneratorConstants.MOSIP_EMPTY_INPUT_ERROR_CODE.getErrorCode(),
					EidGeneratorConstants.MOSIP_EMPTY_INPUT_ERROR_CODE.getErrorMessage());
		}
		if (agentId.length() < 4 || machineId.length() < 5) {

			throw new MosipInputLengthException(EidGeneratorConstants.MOSIP_INPUT_LENGTH_ERROR_CODE.getErrorCode(),
					EidGeneratorConstants.MOSIP_INPUT_LENGTH_ERROR_CODE.getErrorMessage());
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
	public void cleanupId(String agentId, String machineId) {
		agentEid = StringUtil.removeLeftChar(agentId, 4);
		machineEid = StringUtil.removeLeftChar(machineId, 5);

	}

	/**
	 * This method generates a five digit random number
	 * 
	 * @return generated five digit random number
	 */
	public int randomNumberGen() {
		return (10000 + randomNumberGenerator.nextInt(90000));

	}

	/**
	 * This method gets the current timestamp in milliseconds
	 * 
	 * @return current timestamp in thirteen digits
	 */
	public long getCurrTimestamp() {
		return (System.currentTimeMillis());
	}

	/**
	 * This method appends the different strings to generate the EID
	 * 
	 * @param randomDigitEid
	 *            5 digit no. generated
	 * @param currentTimestamp
	 *            current timestamp generated
	 * @return generated EID
	 */
	public String appendString(int randomDigitEid, long currentTimestamp) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(agentEid).append(machineEid).append(randomDigitEid).append(currentTimestamp);
		return (stringBuilder.toString().trim());
	}
}

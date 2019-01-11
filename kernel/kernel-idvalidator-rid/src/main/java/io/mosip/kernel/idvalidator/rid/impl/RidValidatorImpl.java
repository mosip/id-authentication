package io.mosip.kernel.idvalidator.rid.impl;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.kernel.core.util.StringUtils;
import io.mosip.kernel.idvalidator.rid.constant.RidExceptionProperty;
import io.mosip.kernel.idvalidator.rid.constant.RidPropertyConstant;

/**
 * This class validate RID given in string format.
 * 
 * @author Ritesh Sinha
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Component
public class RidValidatorImpl implements RidValidator<String> {

	@Value("${mosip.kernel.rid.length:-1}")
	private int ridLength;

	@Value("${mosip.kernel.rid.centerid-length:-1}")
	private int centerIdLength;

	@Value("${mosip.kernel.rid.machineid-length:-1}")
	private int machineIdLength;

	@Value("${mosip.kernel.rid.timestamp-length:-1}")
	private int timeStampLength;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idvalidator.spi.RidValidator#validateId(java.lang.
	 * Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean validateId(String id, String centerId, String machineId) {
		validateAllInputs(id, centerId, machineId, centerIdLength, machineIdLength, timeStampLength);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idvalidator.spi.RidValidator#validateId(java.lang.
	 * Object)
	 */
	@Override
	public boolean validateId(String id) {
		validateInputs(id, centerIdLength, machineIdLength, timeStampLength);
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idvalidator.spi.RidValidator#validateId(java.lang.
	 * Object, java.lang.Object, java.lang.Object, int, int, int)
	 */
	@Override
	public boolean validateId(String id, String centerId, String machineId, int centerIdLength, int machineIdLength,
			int timeStampLength) {
		validateAllInputs(id, centerId, machineId, centerIdLength, machineIdLength, timeStampLength);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idvalidator.spi.RidValidator#validateId(java.lang.
	 * Object, int, int, int)
	 */
	@Override
	public boolean validateId(String id, int centerIdLength, int machineIdLength, int timeStampLength) {
		validateInputs(id, centerIdLength, machineIdLength, timeStampLength);
		return true;
	}

	private void validateAllInputs(String id, String centerId, String machineId, int centerIdLength,
			int machineIdLength, int timeStampLength) {
		validateInputs(id, centerIdLength, machineIdLength, timeStampLength);
		int endIndex = centerIdLength + machineIdLength;
		if (!id.substring(0, centerIdLength).equals(centerId)) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_CENTER_ID.getErrorCode(),
					RidExceptionProperty.INVALID_CENTER_ID.getErrorMessage());
		}
		if (!id.substring(centerIdLength, endIndex).equals(machineId)) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_DONGLE_ID.getErrorCode(),
					RidExceptionProperty.INVALID_DONGLE_ID.getErrorMessage());
		}
	}

	private void validateInputs(String id, int centerIdLength, int machineIdLength, int timeStampLength) {
		String pattern = RidPropertyConstant.TIME_STAMP_REGEX.getProperty();

		int endIndex = centerIdLength + machineIdLength;

		int timeStampStartIndex = endIndex + 5;

		int timeStampEndIndex = timeStampStartIndex + timeStampLength;
		if (ridLength <= 0 || centerIdLength <= 0 || machineIdLength <= 0 || timeStampLength <= 0) {
			throw new InvalidIDException(
					RidExceptionProperty.INVALID_RIDLENGTH_OR_CENTERIDLENGTH_MACHINEIDLENGTH_TIMESTAMPLENGTH
							.getErrorCode(),
					RidExceptionProperty.INVALID_RIDLENGTH_OR_CENTERIDLENGTH_MACHINEIDLENGTH_TIMESTAMPLENGTH
							.getErrorMessage());
		}
		if (id.length() != ridLength) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_RID_LENGTH.getErrorCode(),
					RidExceptionProperty.INVALID_RID_LENGTH.getErrorMessage() + " :" + ridLength);
		}

		if (!StringUtils.isNumeric(id)) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_RID.getErrorCode(),
					RidExceptionProperty.INVALID_RID.getErrorMessage());
		}
		if (!Pattern.matches(pattern, id.subSequence(timeStampStartIndex, timeStampEndIndex))) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorCode(),
					RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorMessage());
		}
	}

}

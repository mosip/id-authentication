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
 * @since 1.0.0
 *
 */
@Component
public class RidValidatorImpl implements RidValidator<String> {

	@Value("${mosip.kernel.rid.length}")
	private int ridLength;

	@Value("${mosip.kernel.rid.centerid.length}")
	private int centerIdLength;

	@Value("${mosip.kernel.rid.dongleid.length}")
	private int dongleIdLength;

	@Value("${mosip.kernel.rid.timestamp.length}")
	private int timeStampLength;

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.idvalidator.spi.RidValidator#validateId(java.lang.
	 * Object, java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean validateId(String id, String centerId, String dongleId) {

		String pattern = RidPropertyConstant.TIME_STAMP_REGEX.getProperty();
		int endIndex = centerIdLength + dongleIdLength;
		int timeStampStartIndex = endIndex + 5;

		int timeStampEndIndex = timeStampStartIndex + timeStampLength;
		if (!id.substring(0, centerIdLength).equals(centerId)) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_CENTER_ID.getErrorCode(),
					RidExceptionProperty.INVALID_CENTER_ID.getErrorMessage());
		}
		if (!id.substring(centerIdLength, endIndex).equals(dongleId)) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_DONGLE_ID.getErrorCode(),
					RidExceptionProperty.INVALID_DONGLE_ID.getErrorMessage());
		}

		if (id.length() != ridLength) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_RID_LENGTH.getErrorCode(),
					RidExceptionProperty.INVALID_RID_LENGTH.getErrorMessage());
		}

		if (!StringUtils.isNumeric(id)) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_RID.getErrorCode(),
					RidExceptionProperty.INVALID_RID.getErrorMessage());
		}
		if (!Pattern.matches(pattern, id.subSequence(timeStampStartIndex, timeStampEndIndex))) {
			throw new InvalidIDException(RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorCode(),
					RidExceptionProperty.INVALID_RID_TIMESTAMP.getErrorMessage());
		}
		return true;
	}

}

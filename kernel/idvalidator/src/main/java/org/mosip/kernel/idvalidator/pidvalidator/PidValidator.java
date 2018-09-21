/**
 * 
 */
package org.mosip.kernel.idvalidator.pidvalidator;

import java.util.regex.Pattern;

import org.mosip.kernel.core.spi.idvalidator.MosipIdValidator;
import org.mosip.kernel.core.utils.MosipIdChecksum;
import org.mosip.kernel.core.utils.MosipIdFilter;
import org.mosip.kernel.idvalidator.exception.MosipInvalidIDException;
import org.mosip.kernel.idvalidator.pidvalidator.constants.MosipPidExceptionConstants;


public class PidValidator implements MosipIdValidator<String> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.kernel.core.spi.idvalidator.MosipIdValidator#validateId(java.lang.
	 * Object)
	 */
	private static final int LEN = 14;
	private static final String ISNUMERIC_REGEX = "\\d{14}";

	public boolean validateId(String id) {
		// TODO Auto-generated method stub

		if (id == null) {
			throw new MosipInvalidIDException(MosipPidExceptionConstants.PRID_VAL_INVALID_NULL.getErrorCode(),
					MosipPidExceptionConstants.PRID_VAL_INVALID_NULL.getErrorMessage());
		}

		if (id.length() != LEN) {
			throw new MosipInvalidIDException(MosipPidExceptionConstants.PRID_VAL_ILLEGAL_LENGTH.getErrorCode(),
					MosipPidExceptionConstants.PRID_VAL_ILLEGAL_LENGTH.getErrorMessage());
		}

		if (!Pattern.matches(ISNUMERIC_REGEX, id)) {
			throw new MosipInvalidIDException(MosipPidExceptionConstants.PRID_VAL_INVALID_DIGITS.getErrorCode(),
					MosipPidExceptionConstants.PRID_VAL_INVALID_DIGITS.getErrorMessage());
		}

		if (id.charAt(0) == '0' || id.charAt(0) == '1') {
			throw new MosipInvalidIDException(MosipPidExceptionConstants.PRID_VAL_INVALID_ZERO_ONE.getErrorCode(),
					MosipPidExceptionConstants.PRID_VAL_INVALID_ZERO_ONE.getErrorMessage());
		}

		if (!MosipIdFilter.isValidId(id)) {
			throw new MosipInvalidIDException(
					MosipPidExceptionConstants.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorCode(),
					MosipPidExceptionConstants.PRID_VAL_ILLEGAL_SEQUENCE_REPEATATIVE.getErrorMessage());
		}

		if (!MosipIdChecksum.validateChecksum(id)) {
			throw new MosipInvalidIDException(MosipPidExceptionConstants.PRID_VAL_ILLEGAL_CHECKSUM.getErrorCode(),
					MosipPidExceptionConstants.PRID_VAL_ILLEGAL_CHECKSUM.getErrorMessage());
		}

		return true;
	}

}

package io.mosip.kernel.idvalidator.mispid.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.idvalidator.mispid.constant.MispIdExceptionProperty;

/**
 * This class validate MISPID given in string format.
 * 
 * @author Ritesh Sinha
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Component
public class MispIdValidatorImpl implements IdValidator<String> {

	/**
	 * Length of MispId.
	 */
	@Value("${mosip.kernel.mispid.length}")
	private int mispidLength;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idvalidator.spi.IdValidator#validateId(java.lang.Object)
	 */
	@Override
	public boolean validateId(String id) {
		if (id == null || id.trim().isEmpty()) {
			throw new InvalidIDException(MispIdExceptionProperty.INVALID_MISPID.getErrorCode(),
					MispIdExceptionProperty.INVALID_MISPID.getErrorMessage());
		}

		if (id.length() != mispidLength) {
			throw new InvalidIDException(MispIdExceptionProperty.INVALID_MISPID_LENGTH.getErrorCode(),
					MispIdExceptionProperty.INVALID_MISPID_LENGTH.getErrorMessage() + mispidLength);
		}

		return true;
	}

}

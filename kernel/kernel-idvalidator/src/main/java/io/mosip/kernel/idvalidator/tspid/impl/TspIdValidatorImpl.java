package io.mosip.kernel.idvalidator.tspid.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.IdValidator;
import io.mosip.kernel.idvalidator.tspid.constant.TspIdExceptionProperty;

/**
 * This class validate TSPID given in string format.
 * 
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */
@Component
public class TspIdValidatorImpl implements IdValidator<String> {

	/**
	 * Length of TspId.
	 */
	@Value("${mosip.kernel.tspid.length}")
	private int tspidLength;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.idvalidator.spi.IdValidator#validateId(java.lang.Object)
	 */
	@Override
	public boolean validateId(String id) {
		if (id == null || id == "") {
			throw new InvalidIDException(TspIdExceptionProperty.INVALID_TSPID.getErrorCode(),
					TspIdExceptionProperty.INVALID_TSPID.getErrorMessage());
		}

		if (id.length() != tspidLength) {
			throw new InvalidIDException(TspIdExceptionProperty.INVALID_TSPID_LENGTH.getErrorCode(),
					TspIdExceptionProperty.INVALID_TSPID_LENGTH.getErrorMessage() + tspidLength);
		}

		return true;
	}

}

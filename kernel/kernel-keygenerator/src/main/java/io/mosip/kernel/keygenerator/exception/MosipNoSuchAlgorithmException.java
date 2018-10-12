package io.mosip.kernel.keygenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.keygenerator.constants.KeyGeneratorExceptionConstants;
import lombok.NoArgsConstructor;

/**
 * Exception to be thrown when algorithm is not present
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@NoArgsConstructor
public class MosipNoSuchAlgorithmException extends BaseUncheckedException {

	/**
	 * Unique generated id for serialization
	 */
	private static final long serialVersionUID = 3402191559982764137L;

	/**
	 * Constructor for this class
	 * 
	 * @param keyGeneratorExceptionConstants exception code constants
	 */
	public MosipNoSuchAlgorithmException(KeyGeneratorExceptionConstants keyGeneratorExceptionConstants) {
		super(keyGeneratorExceptionConstants.getErrorCode(), keyGeneratorExceptionConstants.getErrorMessage());
	}

}

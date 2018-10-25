package io.mosip.kernel.keygenerator.bouncycastle.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.keygenerator.bouncycastle.constant.KeyGeneratorExceptionConstant;
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
	 * @param keyGeneratorExceptionConstant exception code constants
	 */
	public MosipNoSuchAlgorithmException(KeyGeneratorExceptionConstant keyGeneratorExceptionConstant) {
		super(keyGeneratorExceptionConstant.getErrorCode(), keyGeneratorExceptionConstant.getErrorMessage());
	}

}

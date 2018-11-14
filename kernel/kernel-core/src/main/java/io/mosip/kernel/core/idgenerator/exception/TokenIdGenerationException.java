package io.mosip.kernel.core.idgenerator.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import org.springframework.stereotype.Component;

/**
 * Class handles TokenId generation Exception that which extends {@link BaseUncheckedException}
 * @author Srinivasan
 *
 */
@Component
public class TokenIdGenerationException extends BaseUncheckedException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public TokenIdGenerationException() {
		super();
	}

      /**
	 * Constructor the initialize TokenIdGenerationException
	 * 
	 * @param errorCode
	 *            for this exception
	 * 
	 * @param errorMessage
	 *            for this exception
	 */
	public TokenIdGenerationException(String errorCode, String errorMessage) {
		super(errorCode, errorMessage);
	}
}

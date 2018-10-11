package io.mosip.kernel.idgenerator.tokenid.exception;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import org.springframework.stereotype.Component;

/**
 * @author M1037462
 * @see 1.0.0
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

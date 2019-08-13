package io.mosip.registration.processor.core.logger;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Mukul Puspam
 * Class for logging description and message code
 */
@Data
@Getter
@Setter
public class LogDescription {
	/**
	 * The description
	 */
	private String message;
	/**
	 * The message code
	 */
	private String code;
	/**
	 * The status comment
	 */
	private String statusComment;
	
	private String transactionStatusCode;
	
	private String subStatusCode;
	
	@Override
	public String toString() {
		return this.getMessage();
	}

}

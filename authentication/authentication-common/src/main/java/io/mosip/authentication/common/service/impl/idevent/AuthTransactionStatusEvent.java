package io.mosip.authentication.common.service.impl.idevent;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Instantiates a new auth transaction status event.
 * @author Loganathan Sekar
 */
@Data
public class AuthTransactionStatusEvent {
	
	/** The id. */
	private String id;
	
	/** The transaction id. */
	private String transactionId;
	
	/** The timestamp. */
	private LocalDateTime timestamp;

}

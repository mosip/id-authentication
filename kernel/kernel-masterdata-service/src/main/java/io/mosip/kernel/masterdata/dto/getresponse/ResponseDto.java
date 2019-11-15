package io.mosip.kernel.masterdata.dto.getresponse;

import lombok.Data;


/**
 * Instantiates a new response status dto
 * @author Srinivasan
 * @since 1.0.0
 */
@Data
public class ResponseDto {

	/** The status. */
	private String status;
	
	/** The message. */
	private String message;
}

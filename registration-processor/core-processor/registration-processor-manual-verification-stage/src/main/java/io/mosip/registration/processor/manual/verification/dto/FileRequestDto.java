package io.mosip.registration.processor.manual.verification.dto;

import java.io.Serializable;
import lombok.Data;
	
/**
 * Instantiates a new file request dto.
 * @author Pranav
 * @author Rishabh Keshari
 * 
 */
@Data
public class FileRequestDto implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The reg id. */
	String regId;

}

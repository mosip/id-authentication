package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO class is used to accept the response values for document upload.
 * 
 * @author Rajath Kumar
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class DocumentDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;
	
	/**
	 * File content
	 */
	private byte[] document;

}

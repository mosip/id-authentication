/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the response values when document details are fetched.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DocumentMultipartResponseDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;
	

	/**
	 * Document Name
	 */
	private String docName;
	
	/**
	 * Document Id
	 */
	private String documentId;
	
	/**
	 * Document category
	 */
	private String docCatCode;

	/**
	 * Document Type
	 */
	private String docTypCode;
	
	/**
	 * Language Code
	 */
	private String langCode;
	

}

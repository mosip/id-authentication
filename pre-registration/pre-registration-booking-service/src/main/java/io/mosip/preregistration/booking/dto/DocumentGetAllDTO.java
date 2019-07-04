/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.dto;

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
public class DocumentGetAllDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;
	
	/**
	 * PreRegistration Id
	 */
	private String preregId;
	
	/**
	 * Document Name
	 */
	private String docName;
	
	/**
	 * Document Id
	 */
	private String docId;
	
	/**
	 * Document category
	 */
	private String docCatCode;

	/**
	 * Document Type
	 */
	private String docTypCode;

	/**
	 * Document File Format
	 */
	private String docFileFormat;
	
	/**
	 * File content
	 */
	private byte[] multipartFile;

}

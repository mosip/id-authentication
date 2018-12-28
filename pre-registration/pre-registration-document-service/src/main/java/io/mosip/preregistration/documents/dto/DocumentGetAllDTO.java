/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.dto;

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
	private String prereg_id;
	
	/**
	 * Document Name
	 */
	private String doc_name;
	
	/**
	 * Document Id
	 */
	private String doc_id;
	
	/**
	 * Document category
	 */
	private String doc_cat_code;

	/**
	 * Document Type
	 */
	private String doc_typ_code;

	/**
	 * Document File Format
	 */
	private String doc_file_format;
	
	/**
	 * File content
	 */
	private byte[] MultipartFile;

}

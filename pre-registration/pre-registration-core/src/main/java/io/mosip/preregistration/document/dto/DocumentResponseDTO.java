/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO class is used to accept the response values for document upload.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class DocumentResponseDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;

	/**
	 * PreRegistration Id
	 */
	private String preRegistrationId;
	/**
	 * Document Id
	 */
	private String docId;
	/**
	 * Document Name
	 */
	private String docName;
	/**
	 * Document Category
	 */
	private String docCatCode;
	/**
	 * Document Type
	 */
	private String docTypCode;
	/**
	 * Response Message
	 */
	private String docFileFormat;

}

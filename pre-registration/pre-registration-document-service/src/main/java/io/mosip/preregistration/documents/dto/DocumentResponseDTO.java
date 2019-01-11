/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.dto;

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
	private String preRegsitrationId;
	/**
	 * Document Id
	 */
	private String documnetId;
	/**
	 * Document Name
	 */
	private String documentName;
	/**
	 * Document Category
	 */
	private String documentCat;
	/**
	 * Document Type
	 */
	private String documentType;
	/**
	 * Response Message
	 */
	private String resMsg;

}

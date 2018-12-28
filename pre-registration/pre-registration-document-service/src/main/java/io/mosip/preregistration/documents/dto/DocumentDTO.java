/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the request values for document upload.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class DocumentDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;

	/**
	 * PreRegistration Id
	 */
	private String prereg_id;

	/**
	 * Document Category
	 */
	private String doc_cat_code;

	/**
	 * Document type
	 */
	private String doc_typ_code;

	/**
	 * Document file format
	 */
	private String doc_file_format;

	/**
	 * Status code
	 */
	private String status_code;

	/**
	 * Uploaded Date Time
	 */
	private Date upload_DateTime;

	/**
	 * uploaded By
	 */
	private String upd_by;

}

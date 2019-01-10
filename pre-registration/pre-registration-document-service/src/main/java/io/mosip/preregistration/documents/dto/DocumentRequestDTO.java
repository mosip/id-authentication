/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.dto;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class DocumentRequestDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;

	/**
	 * PreRegistration Id
	 */
	@JsonProperty("pre_registartion_id")
	private String preregId;

	/**
	 * Document Category
	 */
	@JsonProperty("doc_cat_code")
	private String docCatCode;

	/**
	 * Document type
	 */
	@JsonProperty("doc_typ_code")
	private String docTypeCode;

	/**
	 * Document file format
	 */
	@JsonProperty("doc_file_format")
	private String docFileFormat;

	/**
	 * Status code
	 */
	@JsonProperty("status_code")
	private String statusCode;

	/**
	 * Uploaded Date Time
	 */
	@JsonProperty("upload_date_time")
	private Date uploadDateTime;
	
	/**
	 * Uploaded lang code
	 */
	@JsonProperty("lang_code")
	private String langCode;

	/**
	 * uploaded By
	 */
	@JsonProperty("upload_by")
	private String uploadBy;

}

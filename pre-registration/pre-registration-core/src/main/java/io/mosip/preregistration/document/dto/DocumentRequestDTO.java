/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.dto;

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
	 * Document Category
	 */
	@JsonProperty("docCatCode")
	private String docCatCode;

	/**
	 * Document type
	 */
	@JsonProperty("docTypCode")
	private String docTypCode;

	/**
	 * Uploaded lang code
	 */
	@JsonProperty("langCode")
	private String langCode;

}

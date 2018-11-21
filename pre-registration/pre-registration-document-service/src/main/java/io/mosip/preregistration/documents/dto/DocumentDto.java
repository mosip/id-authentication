package io.mosip.preregistration.documents.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Document DTO
 * 
 * @author M1043008
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070542323407937205L;

	@JsonProperty("prereg_id")
	private String prereg_id;

	@JsonProperty("doc_cat_code")
	private String doc_cat_code;

	@JsonProperty("doc_typ_code")
	private String doc_typ_code;

	@JsonProperty("doc_file_format")
	private String doc_file_format;

	@JsonProperty("status_code")
	private String status_code;

//	@JsonProperty("lang_code")
//	private String lang_code;

	@JsonProperty("upload_DateTime")
	private Timestamp upload_DateTime;

	@JsonProperty("upload_by")
	private String upd_by;

}

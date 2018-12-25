package io.mosip.preregistration.documents.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Document DTO
 * 
 * @author M1043008
 *
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class DocumentDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070542323407937205L;

	private String prereg_id;

	private String doc_cat_code;

	private String doc_typ_code;

	private String doc_file_format;

	private String status_code;

	private Date upload_DateTime;

	private String upd_by;

}

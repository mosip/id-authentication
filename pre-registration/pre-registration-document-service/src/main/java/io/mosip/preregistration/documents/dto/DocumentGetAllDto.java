package io.mosip.preregistration.documents.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DocumentGetAllDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070542323407937205L;
	
	
	private String prereg_id;
	
	private String doc_name;
	
	private String doc_id;
	
	private String doc_cat_code;

	private String doc_typ_code;

	private String doc_file_format;
	
	private byte[] MultipartFile;

}

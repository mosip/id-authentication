package io.mosip.registration.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter
@ToString@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {

	private String prereg_id;

	private String group_id;
	
	private boolean is_primary;

	private String doc_cat_code;

	private String doc_typ_code;

	private String doc_file_format;

	private byte[] doc_store;

	private String status_code;

	private String lang_code;

	private String cr_by;

	private Timestamp cr_dtimesz;

	private String upd_by;

	private Timestamp upd_dtimesz;

}

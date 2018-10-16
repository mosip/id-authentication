package io.mosip.registration.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "document", schema = "document")

public class DocumentEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1692781286748263575L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int documentId;

	private String preregId;

	private String doc_name;

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

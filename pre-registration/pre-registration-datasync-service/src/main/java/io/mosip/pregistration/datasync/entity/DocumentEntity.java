package io.mosip.pregistration.datasync.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Document Entity
 * 
 * @author M1046129 - Jagadishwari
 *
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applicant_document", schema = "prereg")
@ToString
public class DocumentEntity implements Serializable {
	private static final long serialVersionUID = 1692781286748263575L;

	@Id
    @SequenceGenerator(name = "applicant_document_id_seq", sequenceName = "applicant_document_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicant_document_id_seq")
	@Column(name = "id")
	private int documentId;

	@Column(name = "prereg_id")
	private String preregId;

	@Column(name = "doc_name")
	private String doc_name;

	@Column(name = "doc_cat_code")
	private String doc_cat_code;

	@Column(name = "doc_typ_code")
	private String doc_typ_code;

	@Column(name = "doc_file_format")
	private String doc_file_format;

	@Column(name = "doc_store")
	private byte[] doc_store;

	@Column(name = "status_code")
	private String status_code;

	@Column(name = "lang_code")
	private String lang_code;

	@Column(name = "cr_by")
	private String cr_by;

	@Column(name = "cr_dtimes")
	private Timestamp cr_dtimesz;

	@Column(name = "upd_by")
	private String upd_by;

	@Column(name = "upd_dtimes")
	private Timestamp upd_dtimesz;

}

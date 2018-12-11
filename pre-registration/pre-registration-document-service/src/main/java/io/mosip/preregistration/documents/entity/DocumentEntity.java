package io.mosip.preregistration.documents.entity;

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

/**
 * Document Entity
 * 
 * @author M1037717
 *
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applicant_document", schema = "prereg")
public class DocumentEntity implements Serializable {
	private static final long serialVersionUID = 1692781286748263575L;

	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
    @SequenceGenerator(name = "applicant_document_id_seq", sequenceName = "applicant_document_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicant_document_id_seq")
	@Column(name = "id")
	private int documentId;

	@Column(name = "prereg_id")
	private String preregId;

	@Column(name = "doc_name")
	private String docName;

	@Column(name = "doc_cat_code")
	private String docCatCode;

	@Column(name = "doc_typ_code")
	private String docTypeCode;

	@Column(name = "doc_file_format")
	private String docFileFormat;

	@Column(name = "doc_store")
	private byte[] docStore;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "lang_code")
	private String langCode;

	@Column(name = "cr_by")
	private String crBy;

	@Column(name = "cr_dtimes")
	private Timestamp crDtime;

	@Column(name = "upd_by")
	private String updBy;

	@Column(name = "upd_dtimes")
	private Timestamp updDtime;

}

/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This entity class defines the database table details for Document.
 * 
 * @author Rajath KR
 * @author Kishan Rathore
 * @author Sanober Noor
 * @author Tapaswini Bahera
 * @author Jagadishwari S
 * @since 1.0.0
 *
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "applicant_document", schema = "prereg")
@NamedQuery(name = "DocumentEntity.findSingleDocument", query = "SELECT d FROM DocumentEntity d WHERE d.preregId= :preId AND d.docCatCode= :catCode")
public class DocumentEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1692781286748263575L;

	/**
	 * Document Id
	 */
	@Id
	@SequenceGenerator(name = "applicant_document_id_seq", sequenceName = "applicant_document_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "applicant_document_id_seq")
	@Column(name = "id")
	private int documentId;

	/**
	 * PreRegistration Id
	 */
	@Column(name = "prereg_id")
	private String preregId;

	/**
	 * Document Name
	 */
	@Column(name = "doc_name")
	private String docName;

	/**
	 * Document Category
	 */
	@Column(name = "doc_cat_code")
	private String docCatCode;

	/**
	 * Document Type
	 */
	@Column(name = "doc_typ_code")
	private String docTypeCode;

	/**
	 * Document File Format
	 */
	@Column(name = "doc_file_format")
	private String docFileFormat;

	/**
	 * Status Code
	 */
	@Column(name = "status_code")
	private String statusCode;

	/**
	 * Language Code
	 */
	@Column(name = "lang_code")
	private String langCode;

	/**
	 * Created By
	 */
	@Column(name = "cr_by")
	private String crBy;

	/**
	 * Created Date Time
	 */
	@Column(name = "cr_dtimes")
	private Timestamp crDtime;

	/**
	 * Updated By
	 */
	@Column(name = "upd_by")
	private String updBy;

	/**
	 * Updated Date Time
	 */
	@Column(name = "upd_dtimes")
	private Timestamp updDtime;

}

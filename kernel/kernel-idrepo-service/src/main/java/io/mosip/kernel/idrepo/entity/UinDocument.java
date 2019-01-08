package io.mosip.kernel.idrepo.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * The Class UinDocument.
 *
 * @author Manoj SP
 */
@Getter
@Setter
@ToString(exclude = {"uin"})
@Entity
@NoArgsConstructor
@Table(schema = "idrepo")
public class UinDocument {
	
	public UinDocument(String uinRefId, String doccatCode, String doctypCode, String docId, String docName,
			String docfmtCode, String docHash, String langCode, String createdBy, LocalDateTime createdDateTime,
			String updatedBy, LocalDateTime updatedDateTime, Boolean isDeleted, LocalDateTime deletedDateTime) {
		super();
		this.uinRefId = uinRefId;
		this.doccatCode = doccatCode;
		this.doctypCode = doctypCode;
		this.docId = docId;
		this.docName = docName;
		this.docfmtCode = docfmtCode;
		this.docHash = docHash;
		this.langCode = langCode;
		this.createdBy = createdBy;
		this.createdDateTime = createdDateTime;
		this.updatedBy = updatedBy;
		this.updatedDateTime = updatedDateTime;
		this.isDeleted = isDeleted;
		this.deletedDateTime = deletedDateTime;
	}

	/** The uin ref id. */
	@Id
	private String uinRefId;
	
	/** The doccat code. */
	private String doccatCode;
	
	/** The doctyp code. */
	private String doctypCode;
	
	/** The doc id. */
	private String docId;
	
	/** The doc name. */
	private String docName;
	
	/** The docfmt code. */
	private String docfmtCode;
	
	/** The doc hash. */
	private String docHash;
	
	/** The lang code. */
	private String langCode;

	/** The created by. */
	@Column(name = "cr_by")
	private String createdBy;
	
	/** The created date time. */
	@Column(name = "cr_dtimes")
	private LocalDateTime createdDateTime;
	
	/** The updated by. */
	@Column(name = "upd_by")
	private String updatedBy;
	
	/** The updated date time. */
	@Column(name = "upd_dtimes")
	private LocalDateTime updatedDateTime;
	
	/** The is deleted. */
	private Boolean isDeleted;
	
	/** The deleted date time. */
	@Column(name = "del_dtimes")
	private LocalDateTime deletedDateTime;
	
	@ManyToOne
	@JoinColumn(name = "uinRefId", insertable = false, updatable = false)
	@Setter(value = AccessLevel.NONE)
	private Uin uin;
}

package io.mosip.kernel.idrepo.entity;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// TODO: Auto-generated Javadoc
/**
 * The Class UinDocumentHistory.
 *
 * @author Manoj SP
 */

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@IdClass(HistoryPK.class)
@Table(name = "uin_document_h", schema = "idrepo")
public class UinDocumentHistory {
	
	/** The uin ref id. */
	@Id
	private String uinRefId;
	
	/** The effective date time. */
	@Id
	@Column(name = "eff_dtimes")
	private LocalDateTime effectiveDateTime;
	
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
}

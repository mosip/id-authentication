package io.mosip.registration.processor.packet.storage.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * The Class ApplicantDocumentEntity.
 *
 * @author Horteppa M1048399 The persistent class for the applicant_document
 *         database table.
 */
@Entity
@Table(name = "applicant_document", schema = "regprc")
public class ApplicantDocumentEntity extends BasePacketEntity<ApplicantDocumentPKEntity> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The cr by. */
	@Column(name = "cr_by")
	private String crBy = "MOSIP_SYSTEM";

	/** The cr dtimes. */
	@Column(name = "cr_dtimes", nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime crDtimes;

	/** The doc file format. */
	@Column(name = "doc_file_format", nullable = false)
	private String docFileFormat;

	/** The doc name. */
	@Column(name = "doc_name", nullable = false)
	private String docName;

	/** The doc owner. */
	@Column(name = "doc_owner")
	private String docOwner;

	/** The doc store. */
	@Column(name = "doc_store")
	private byte[] docStore;

	/** The pre reg id. */
	@Column(name = "prereg_id", nullable = false)
	private String preRegId;

	/** The upd by. */
	@Column(name = "upd_by")
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimes. */
	@Column(name = "upd_dtimes")
	@UpdateTimestamp
	private LocalDateTime updDtimes;

	/** The del dtimes. */
	@Column(name = "del_dtimes")
	@UpdateTimestamp
	private LocalDateTime delDtimes;

	/** The is active. */
	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	/** The is deleted. */
	@Column(name = "is_deleted")
	private Boolean isDeleted;

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Sets the active.
	 *
	 * @param isActive
	 *            the new active
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Instantiates a new applicant document entity.
	 */
	public ApplicantDocumentEntity() {
		super();
	}

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return this.crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy
	 *            the new cr by
	 */
	public void setCrBy(String crBy) {
		this.crBy = crBy;
	}

	/**
	 * Gets the cr dtimes.
	 *
	 * @return the cr dtimes
	 */
	public LocalDateTime getCrDtimes() {
		return this.crDtimes;
	}

	/**
	 * Sets the cr dtimes.
	 *
	 * @param crDtimes
	 *            the new cr dtimes
	 */
	public void setCrDtimes(LocalDateTime crDtimes) {
		this.crDtimes = crDtimes;
	}

	/**
	 * Gets the doc file format.
	 *
	 * @return the doc file format
	 */
	public String getDocFileFormat() {
		return this.docFileFormat;
	}

	/**
	 * Sets the doc file format.
	 *
	 * @param docFileFormat
	 *            the new doc file format
	 */
	public void setDocFileFormat(String docFileFormat) {
		this.docFileFormat = docFileFormat;
	}

	/**
	 * Gets the doc name.
	 *
	 * @return the doc name
	 */
	public String getDocName() {
		return this.docName;
	}

	/**
	 * Sets the doc name.
	 *
	 * @param docName
	 *            the new doc name
	 */
	public void setDocName(String docName) {
		this.docName = docName;
	}

	/**
	 * Gets the doc owner.
	 *
	 * @return the doc owner
	 */
	public String getDocOwner() {
		return this.docOwner;
	}

	/**
	 * Sets the doc owner.
	 *
	 * @param docOwner
	 *            the new doc owner
	 */
	public void setDocOwner(String docOwner) {
		this.docOwner = docOwner;
	}

	/**
	 * Gets the doc store.
	 *
	 * @return the doc store
	 */
	public byte[] getDocStore() {
		return this.docStore;
	}

	/**
	 * Sets the doc store.
	 *
	 * @param docStore
	 *            the new doc store
	 */
	public void setDocStore(byte[] docStore) {
		this.docStore = docStore;
	}

	/**
	 * Gets the pre reg id.
	 *
	 * @return the pre reg id
	 */
	public String getPreRegId() {
		return this.preRegId;
	}

	/**
	 * Sets the pre reg id.
	 *
	 * @param preregId
	 *            the new pre reg id
	 */
	public void setPreRegId(String preregId) {
		this.preRegId = preregId;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the upd by
	 */
	public String getUpdBy() {
		return this.updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy
	 *            the new upd by
	 */
	public void setUpdBy(String updBy) {
		this.updBy = updBy;
	}

	/**
	 * Gets the upd dtimes.
	 *
	 * @return the upd dtimes
	 */
	public LocalDateTime getUpdDtimes() {
		return this.updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimes
	 *            the new upd dtimes
	 */
	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}

}
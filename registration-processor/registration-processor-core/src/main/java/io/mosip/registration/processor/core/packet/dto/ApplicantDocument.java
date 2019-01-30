
package io.mosip.registration.processor.core.packet.dto;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * The Class ApplicantDocument.
 *
 * @author M1047487
 */
public class ApplicantDocument {
	
	/** The cr by. */
	private String crBy;

	/** The cr dtimes. */
	private LocalDateTime crDtimes;

	/** The doc file format. */
	private String docFileFormat;

	/** The doc name. */
	private String docName;

	/** The doc owner. */
	private String docOwner;

	/** The doc store. */
	private byte[] docStore;

	/** The pre reg id. */
	private String preRegId;

	/** The upd by. */
	private String updBy = "MOSIP_SYSTEM";

	/** The upd dtimes. */
	/* The upd dtimes. */
	private LocalDateTime updDtimes;

	/** The del dtimes. */
	private LocalDateTime delDtimes;

	/** The is active. */
	private Boolean isActive;

	/** The is deleted. */
	private Boolean isDeleted;

	/**
	 * Gets the cr by.
	 *
	 * @return the cr by
	 */
	public String getCrBy() {
		return crBy;
	}

	/**
	 * Sets the cr by.
	 *
	 * @param crBy the new cr by
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
		return crDtimes;
	}

	/**
	 * Sets the cr dtimes.
	 *
	 * @param crDtimes the new cr dtimes
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
		return docFileFormat;
	}

	/**
	 * Sets the doc file format.
	 *
	 * @param docFileFormat the new doc file format
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
		return docName;
	}

	/**
	 * Sets the doc name.
	 *
	 * @param docName the new doc name
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
		return docOwner;
	}

	/**
	 * Sets the doc owner.
	 *
	 * @param docOwner the new doc owner
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
		return Arrays.copyOf(docStore,docStore.length);
	}

	/**
	 * Sets the doc store.
	 *
	 * @param docStore the new doc store
	 */
	public void setDocStore(byte[] docStore) {
		this.docStore = docStore!=null?docStore:null;
	}

	/**
	 * Gets the pre reg id.
	 *
	 * @return the pre reg id
	 */
	public String getPreRegId() {
		return preRegId;
	}

	/**
	 * Sets the pre reg id.
	 *
	 * @param preRegId the new pre reg id
	 */
	public void setPreRegId(String preRegId) {
		this.preRegId = preRegId;
	}

	/**
	 * Gets the upd by.
	 *
	 * @return the upd by
	 */
	public String getUpdBy() {
		return updBy;
	}

	/**
	 * Sets the upd by.
	 *
	 * @param updBy the new upd by
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
		return updDtimes;
	}

	/**
	 * Sets the upd dtimes.
	 *
	 * @param updDtimes the new upd dtimes
	 */
	public void setUpdDtimes(LocalDateTime updDtimes) {
		this.updDtimes = updDtimes;
	}

	/**
	 * Gets the del dtimes.
	 *
	 * @return the del dtimes
	 */
	public LocalDateTime getDelDtimes() {
		return delDtimes;
	}

	/**
	 * Sets the del dtimes.
	 *
	 * @param delDtimes the new del dtimes
	 */
	public void setDelDtimes(LocalDateTime delDtimes) {
		this.delDtimes = delDtimes;
	}

	/**
	 * Gets the checks if is active.
	 *
	 * @return the checks if is active
	 */
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	 * Sets the checks if is active.
	 *
	 * @param isActive the new checks if is active
	 */
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * Gets the checks if is deleted.
	 *
	 * @return the checks if is deleted
	 */
	public Boolean getIsDeleted() {
		return isDeleted;
	}

	/**
	 * Sets the checks if is deleted.
	 *
	 * @param isDeleted the new checks if is deleted
	 */
	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
}
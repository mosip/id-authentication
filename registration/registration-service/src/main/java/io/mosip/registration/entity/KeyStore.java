package io.mosip.registration.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Entity
@Table(name = "key_store", schema = "reg")
public class KeyStore {
	@Id
	@Column(name = "id")
	private String id;
	@Column(name = "public_key")
	private byte[] publicKey;
	@Column(name = "valid_from_dtimes")
	private Timestamp validFromDtimes;
	@Column(name = "valid_till_dtimes")
	private Timestamp validTillDtimes;
	@Column(name = "ref_id")
	private String refId;
	@Column(name = "status_code")
	private String statusCode;
	@Column(name = "cr_by")
	private String createdBy;
	@Column(name = "cr_dtimes")
	private Timestamp createdDtimes;
	@Column(name = "upd_by")
	private String updatedBy;
	@Column(name = "upd_dtimes")
	private Timestamp updatedTimes;
	@Column(name = "is_deleted")
	private boolean isDeleted;
	@Column(name = "del_dtimes")
	private Timestamp deletedTimes;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the publicKey
	 */
	public byte[] getPublicKey() {
		return publicKey;
	}

	/**
	 * @param publicKey
	 *            the publicKey to set
	 */
	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	/**
	 * @return the validFromDtimes
	 */
	public Timestamp getValidFromDtimes() {
		return validFromDtimes;
	}

	/**
	 * @param validFromDtimes
	 *            the validFromDtimes to set
	 */
	public void setValidFromDtimes(Timestamp validFromDtimes) {
		this.validFromDtimes = validFromDtimes;
	}

	/**
	 * @return the validTillDtimes
	 */
	public Timestamp getValidTillDtimes() {
		return validTillDtimes;
	}

	/**
	 * @param validTillDtimes
	 *            the validTillDtimes to set
	 */
	public void setValidTillDtimes(Timestamp validTillDtimes) {
		this.validTillDtimes = validTillDtimes;
	}

	/**
	 * @return the refId
	 */
	public String getRefId() {
		return refId;
	}

	/**
	 * @param refId
	 *            the refId to set
	 */
	public void setRefId(String refId) {
		this.refId = refId;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDtimes
	 */
	public Timestamp getCreatedDtimes() {
		return createdDtimes;
	}

	/**
	 * @param createdDtimes
	 *            the createdDtimes to set
	 */
	public void setCreatedDtimes(Timestamp createdDtimes) {
		this.createdDtimes = createdDtimes;
	}

	/**
	 * @return the updatedBy
	 */
	public String getUpdatedBy() {
		return updatedBy;
	}

	/**
	 * @param updatedBy
	 *            the updatedBy to set
	 */
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	/**
	 * @return the updatedTimes
	 */
	public Timestamp getUpdatedTimes() {
		return updatedTimes;
	}

	/**
	 * @param updatedTimes
	 *            the updatedTimes to set
	 */
	public void setUpdatedTimes(Timestamp updatedTimes) {
		this.updatedTimes = updatedTimes;
	}

	/**
	 * @return the isDeleted
	 */
	public boolean isDeleted() {
		return isDeleted;
	}

	/**
	 * @param isDeleted
	 *            the isDeleted to set
	 */
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	/**
	 * @return the deletedTimes
	 */
	public Timestamp getDeletedTimes() {
		return deletedTimes;
	}

	/**
	 * @param deletedTimes
	 *            the deletedTimes to set
	 */
	public void setDeletedTimes(Timestamp deletedTimes) {
		this.deletedTimes = deletedTimes;
	}

}

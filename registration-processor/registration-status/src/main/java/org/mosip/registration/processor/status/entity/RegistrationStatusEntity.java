package org.mosip.registration.processor.status.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.stereotype.Component;

@Component
@Entity
@Table(name = "enrolment_status")
public class RegistrationStatusEntity {

	@Column(name = "enrolment_id")
	@Id
	private String enrolmentId;

	@Column(name = "enrolment_status")
	private String status;

	@Column(name = "retry_count")
	private Integer retryCount;

	@Column
	@CreationTimestamp
	private LocalDateTime createDateTime;

	@Column
	@UpdateTimestamp
	private LocalDateTime updateDateTime;

	public RegistrationStatusEntity() {
		super();
	}

	public RegistrationStatusEntity(String enrolmentId, String status, Integer retryCount) {
		super();
		this.enrolmentId = enrolmentId;
		this.status = status;
		this.retryCount = retryCount;
	}

	/**
	 * Get registration id from registration status table.
	 * 
	 * @return the enrolmentId
	 */
	public String getEnrolmentId() {
		return enrolmentId;
	}

	/**
	 * Get registration status code from registration status table.
	 * 
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Get retry count from registration status table.
	 * 
	 * @return the retryCount
	 */
	public Integer getRetryCount() {
		return retryCount;
	}

	/**
	 * Set registration id to registration status table.
	 * 
	 * @param enrolmentId
	 *            the enrolmentId to set
	 */
	public void setEnrolmentId(String enrolmentId) {
		this.enrolmentId = enrolmentId;
	}

	/**
	 * Set registration status code to registration status table.
	 * 
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Set retry count to registration status table.
	 * 
	 * @param retryCount
	 *            the retryCount to set
	 */
	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * Gets creation date time of registration record.
	 * 
	 * @return LocalDateTime
	 */
	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	/**
	 * Sets creation date time of registration record.
	 * 
	 * @param LocalDateTime
	 */
	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}

	/**
	 * Gets updated date time of registration record.
	 * 
	 * @return LocalDateTime
	 */
	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	/**
	 * Sets updated date time of registration record.
	 * 
	 * @param LocalDateTime
	 */
	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

}

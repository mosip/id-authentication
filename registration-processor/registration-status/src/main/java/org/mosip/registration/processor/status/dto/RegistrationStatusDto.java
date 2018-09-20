package org.mosip.registration.processor.status.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RegistrationStatusDto implements Serializable {

	private static final long serialVersionUID = 6705845720255847210L;
	private String enrolmentId;
	private String status;
	private Integer retryCount;
	private LocalDateTime createDateTime;
	private LocalDateTime updateDateTime;

	public RegistrationStatusDto() {
		super();
	}

	public RegistrationStatusDto(String enrolmentId, String status, Integer retryCount, LocalDateTime createDateTime,
			LocalDateTime updateDateTime) {
		super();
		this.enrolmentId = enrolmentId;
		this.status = status;
		this.retryCount = retryCount;
		this.createDateTime = createDateTime;
		this.updateDateTime = updateDateTime;
	}

	public String getEnrolmentId() {
		return enrolmentId;
	}

	public void setEnrolmentId(String enrolmentId) {
		this.enrolmentId = enrolmentId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(Integer retryCount) {
		this.retryCount = retryCount;
	}

	public LocalDateTime getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(LocalDateTime createDateTime) {
		this.createDateTime = createDateTime;
	}

	public LocalDateTime getUpdateDateTime() {
		return updateDateTime;
	}

	public void setUpdateDateTime(LocalDateTime updateDateTime) {
		this.updateDateTime = updateDateTime;
	}

}

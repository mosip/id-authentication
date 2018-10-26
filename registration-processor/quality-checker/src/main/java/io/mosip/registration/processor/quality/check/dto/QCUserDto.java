package io.mosip.registration.processor.quality.check.dto;

import java.io.Serializable;

public class QCUserDto implements Serializable{

	private String qcUserId;
	private String regId;
	private String decisionStatus;

	public QCUserDto() {
		super();
	}

	public String getQcUserId() {
		return qcUserId;
	}

	public void setQcUserId(String qcUserId) {
		this.qcUserId = qcUserId;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public String getDecisionStatus() {
		return decisionStatus;
	}

	public void setDecisionStatus(String decisionStatus) {
		this.decisionStatus = decisionStatus;
	}

}

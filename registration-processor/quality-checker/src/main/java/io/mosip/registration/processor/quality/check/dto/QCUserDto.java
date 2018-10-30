package io.mosip.registration.processor.quality.check.dto;

import java.io.Serializable;

import lombok.Data;
public class QCUserDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String qcUserId;
	private String regId;
	private DecisionStatus decisionStatus;
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
	public DecisionStatus getDecisionStatus() {
		return decisionStatus;
	}
	public void setDecisionStatus(DecisionStatus decisionStatus) {
		this.decisionStatus = decisionStatus;
	}



}

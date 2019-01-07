package io.mosip.registration.processor.quality.check.dto;

import java.io.Serializable;

/**
 * The Class QCUserDto.
 */
public class QCUserDto implements Serializable{

		
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The qc user id. */
	private String qcUserId;
	
	/** The reg id. */
	private String regId;
	
	/** The decision status. */
	private DecisionStatus decisionStatus;
	
	/**
	 * Gets the qc user id.
	 *
	 * @return the qc user id
	 */
	public String getQcUserId() {
		return qcUserId;
	}
	
	/**
	 * Sets the qc user id.
	 *
	 * @param qcUserId the new qc user id
	 */
	public void setQcUserId(String qcUserId) {
		this.qcUserId = qcUserId;
	}
	
	/**
	 * Gets the reg id.
	 *
	 * @return the reg id
	 */
	public String getRegId() {
		return regId;
	}
	
	/**
	 * Sets the reg id.
	 *
	 * @param regId the new reg id
	 */
	public void setRegId(String regId) {
		this.regId = regId;
	}
	
	/**
	 * Gets the decision status.
	 *
	 * @return the decision status
	 */
	public DecisionStatus getDecisionStatus() {
		return decisionStatus;
	}
	
	/**
	 * Sets the decision status.
	 *
	 * @param decisionStatus the new decision status
	 */
	public void setDecisionStatus(DecisionStatus decisionStatus) {
		this.decisionStatus = decisionStatus;
	}



}

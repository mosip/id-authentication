package org.mosip.registration.dto;

/**
 * This class contains the Registration Operator Specific Information
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class OSIDataDTO extends BaseDTO {
	private String operatorUIN;
	private String operatorName;
	private String operatorUserID;
	private String supervisorUIN;
	private String supervisorName;
	private String supervisorUserID;
	// Below fields are used for Introducer or HOF
	private String introducerType;

	/**
	 * @return the operatorUIN
	 */
	public String getOperatorUIN() {
		return operatorUIN;
	}

	/**
	 * @param operatorUIN
	 *            the operatorUIN to set
	 */
	public void setOperatorUIN(String operatorUIN) {
		this.operatorUIN = operatorUIN;
	}

	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * @param operatorName
	 *            the operatorName to set
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * @return the operatorUserID
	 */
	public String getOperatorUserID() {
		return operatorUserID;
	}

	/**
	 * @param operatorUserID
	 *            the operatorUserID to set
	 */
	public void setOperatorUserID(String operatorUserID) {
		this.operatorUserID = operatorUserID;
	}

	/**
	 * @return the supervisorUIN
	 */
	public String getSupervisorUIN() {
		return supervisorUIN;
	}

	/**
	 * @param supervisorUIN
	 *            the supervisorUIN to set
	 */
	public void setSupervisorUIN(String supervisorUIN) {
		this.supervisorUIN = supervisorUIN;
	}

	/**
	 * @return the supervisorName
	 */
	public String getSupervisorName() {
		return supervisorName;
	}

	/**
	 * @param supervisorName
	 *            the supervisorName to set
	 */
	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}

	/**
	 * @return the supervisorUserID
	 */
	public String getSupervisorUserID() {
		return supervisorUserID;
	}

	/**
	 * @param supervisorUserID
	 *            the supervisorUserID to set
	 */
	public void setSupervisorUserID(String supervisorUserID) {
		this.supervisorUserID = supervisorUserID;
	}

	/**
	 * @return the introducerType
	 */
	public String getIntroducerType() {
		return introducerType;
	}

	/**
	 * @param introducerType
	 *            the introducerType to set
	 */
	public void setIntroducerType(String introducerType) {
		this.introducerType = introducerType;
	}
}

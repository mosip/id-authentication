package io.mosip.registration.dto;

/**
 * This class contains the Registration Operator Specific Information
 * 
 * @author Dinesh Asokan
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public class OSIDataDTO extends BaseDTO {

	private String operatorID;
	private String supervisorID;
	// Below fields are used for Introducer or HOF or Parent
	private String introducerType;

	/**
	 * @return the operatorID
	 */
	public String getOperatorID() {
		return operatorID;
	}

	/**
	 * @param operatorID
	 *            the operatorID to set
	 */
	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}

	/**
	 * @return the supervisorID
	 */
	public String getSupervisorID() {
		return supervisorID;
	}

	/**
	 * @param supervisorID
	 *            the supervisorID to set
	 */
	public void setSupervisorID(String supervisorID) {
		this.supervisorID = supervisorID;
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

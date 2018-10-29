package io.mosip.registration.dto;

/**
 * This class contains the Registration Operator Specific Information
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class OSIDataDTO extends BaseDTO {
	
	private String operatorID;
	private String supervisorID;
	private String supervisorName;
	// Below fields are used for Introducer or HOF or Parent
	private String introducerType;
	private String introducerName;
	public String getOperatorID() {
		return operatorID;
	}
	public void setOperatorID(String operatorID) {
		this.operatorID = operatorID;
	}
	public String getSupervisorID() {
		return supervisorID;
	}
	public void setSupervisorID(String supervisorID) {
		this.supervisorID = supervisorID;
	}
	public String getSupervisorName() {
		return supervisorName;
	}
	public void setSupervisorName(String supervisorName) {
		this.supervisorName = supervisorName;
	}
	public String getIntroducerType() {
		return introducerType;
	}
	public void setIntroducerType(String introducerType) {
		this.introducerType = introducerType;
	}
	public String getIntroducerName() {
		return introducerName;
	}
	public void setIntroducerName(String introducerName) {
		this.introducerName = introducerName;
	}
	
}

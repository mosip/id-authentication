package io.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;

/**
 * User machine mapping DTO
 * 
 * @author Dinesh Ashokan
 *
 */
public class UserMachineMappingDTO {
	private SimpleStringProperty userID;
	private SimpleStringProperty userName;
	private SimpleStringProperty status;
	private SimpleStringProperty role;

	private String centreID;

	private String stationID;
	private String machineID;

	public UserMachineMappingDTO() {
		super();
	}

	/**
	 * UserMachineMapping constructor is to create DTO
	 * 
	 * @param userID
	 * @param userName
	 * @param role
	 * @param status
	 * @param centreID
	 * @param stationID
	 * @param machineID
	 */
	public UserMachineMappingDTO(String userID, String userName, String role, String status, String centreID,
			String stationID, String machineID) {
		super();
		this.userID = new SimpleStringProperty(userID);
		this.userName = new SimpleStringProperty(userName);
		this.role = new SimpleStringProperty(role);
		this.status = new SimpleStringProperty(status);
		this.centreID = centreID;
		this.stationID = stationID;
		this.machineID = machineID;
	}

	public String getUserID() {
		return userID.get();
	}

	public void setUserID(String userID) {
		this.userID.set(userID);
	}

	public String getUserName() {
		return userName.get();
	}

	public void setUserName(String userName) {
		this.userName.set(userName);
	}

	public String getRole() {
		return role.get();
	}

	public void setRole(String role) {
		this.role.set(role);

	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public String getCentreID() {
		return centreID;
	}

	public void setCentreID(String centreID) {
		this.centreID = centreID;
	}

	public String getStationID() {
		return stationID;
	}

	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	public String getMachineID() {
		return machineID;
	}

	public void setMachineID(String machineID) {
		this.machineID = machineID;
	}

}

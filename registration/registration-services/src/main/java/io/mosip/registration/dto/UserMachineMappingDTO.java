package io.mosip.registration.dto;

import javafx.beans.property.SimpleStringProperty;

/**
 * The DTO Class UserMachineMappingDTO.
 *
 * @author Dinesh Ashokan
 * @version 1.0.0
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
	 * UserMachineMapping constructor is to create DTO.
	 *
	 * @param userID 
	 * 				the user ID
	 * @param userName 
	 * 				the user name
	 * @param role 
	 * 				the role
	 * @param status 
	 * 				the status
	 * @param centreID 
	 * 				the centre ID
	 * @param stationID 
	 * 				the station ID
	 * @param machineID 
	 * 				the machine ID
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

	/**
	 * Gets the user ID.
	 *
	 * @return the user ID
	 */
	public String getUserID() {
		return userID.get();
	}

	/**
	 * Sets the user ID.
	 *
	 * @param userID the new user ID
	 */
	public void setUserID(String userID) {
		this.userID.set(userID);
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return userName.get();
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName the new user name
	 */
	public void setUserName(String userName) {
		this.userName.set(userName);
	}

	/**
	 * Gets the role.
	 *
	 * @return the role
	 */
	public String getRole() {
		return role.get();
	}

	/**
	 * Sets the role.
	 *
	 * @param role the new role
	 */
	public void setRole(String role) {
		this.role.set(role);

	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status.get();
	}

	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(String status) {
		this.status.set(status);
	}

	/**
	 * Gets the centre ID.
	 *
	 * @return the centre ID
	 */
	public String getCentreID() {
		return centreID;
	}

	/**
	 * Sets the centre ID.
	 *
	 * @param centreID the new centre ID
	 */
	public void setCentreID(String centreID) {
		this.centreID = centreID;
	}

	/**
	 * Gets the station ID.
	 *
	 * @return the station ID
	 */
	public String getStationID() {
		return stationID;
	}

	/**
	 * Sets the station ID.
	 *
	 * @param stationID the new station ID
	 */
	public void setStationID(String stationID) {
		this.stationID = stationID;
	}

	/**
	 * Gets the machine ID.
	 *
	 * @return the machine ID
	 */
	public String getMachineID() {
		return machineID;
	}

	/**
	 * Sets the machine ID.
	 *
	 * @param machineID the new machine ID
	 */
	public void setMachineID(String machineID) {
		this.machineID = machineID;
	}

}

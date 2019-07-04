package io.mosip.registration.controller.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class PacketStatusVO {

	private SimpleStringProperty fileName;
	private SimpleStringProperty packetClientStatus;
	private SimpleStringProperty packetServerStatus;
	private BooleanProperty status = new SimpleBooleanProperty(false);
	private SimpleStringProperty packetPath;
	private SimpleStringProperty uploadStatus;
	private SimpleStringProperty clientStatusComments;
	private SimpleStringProperty packetStatus;
	private SimpleStringProperty supervisorStatus;
	private SimpleStringProperty supervisorComments;
	private SimpleStringProperty createdTime;
	private SimpleStringProperty slno;
	
	/**
	 * @return the supervisorStatus
	 */
	public String getSupervisorStatus() {
		return supervisorStatus.get();
	}

	/**
	 * @param supervisorStatus the supervisorStatus to set
	 */
	public void setSupervisorStatus(String supervisorStatus) {
		this.supervisorStatus = new SimpleStringProperty(supervisorStatus);
	}

	/**
	 * @return the supervisorComments
	 */
	public String getSupervisorComments() {
		return supervisorComments.get();
	}

	/**
	 * @param supervisorComments the supervisorComments to set
	 */
	public void setSupervisorComments(String supervisorComments) {
		this.supervisorComments = new SimpleStringProperty(supervisorComments);
	}

	public String getFileName() {
		return fileName.get();
	}

	public void setFileName(String fileName) {
		this.fileName = new SimpleStringProperty(fileName);
	}

	public String getPacketClientStatus() {
		return packetClientStatus.get();
	}

	public void setPacketClientStatus(String packetClientStatus) {
		this.packetClientStatus = new SimpleStringProperty(packetClientStatus);
	}

	public String getPacketServerStatus() {
		return packetServerStatus.get();
	}

	public void setPacketServerStatus(String packetServerStatus) {
		this.packetServerStatus = new SimpleStringProperty(packetServerStatus);
	}

	public String getPacketPath() {
		return packetPath.get();
	}

	public void setPacketPath(String packetPath) {
		this.packetPath = new SimpleStringProperty(packetPath);
	}

	public String getUploadStatus() {
		return uploadStatus.get();
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = new SimpleStringProperty(uploadStatus);
	}

	public String getClientStatusComments() {
		return clientStatusComments.get();
	}

	public void setClientStatusComments(String clientStatusComments) {
		this.clientStatusComments = new SimpleStringProperty(clientStatusComments);
	}

	public Boolean getStatus() {
		return status.get();
	}
	
	 public BooleanProperty selectedProperty() {
         return status;
     }

	public void setStatus(Boolean status) {
		this.status.set(status);
	}

	public String getPacketStatus() {
		return packetStatus.get();
	}

	public void setPacketStatus(String packetStatus) {
		this.packetStatus = new SimpleStringProperty(packetStatus);
	}

	public String getCreatedTime() {
		return createdTime.get();
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = new SimpleStringProperty(createdTime);
	}

	public String getSlno() {
		return slno.get();
	}

	public void setSlno(String slno) {
		this.slno = new SimpleStringProperty(slno);
	}
	
}

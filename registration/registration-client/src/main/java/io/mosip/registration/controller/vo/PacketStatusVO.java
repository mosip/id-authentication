package io.mosip.registration.controller.vo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PacketStatusVO {



	private String fileName;
	private String packetClientStatus;
	private String packetServerStatus;
	private BooleanProperty status = new SimpleBooleanProperty(false);
	private String packetPath;
	private String uploadStatus;
	private String clientStatusComments;
	private String packetStatus;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPacketClientStatus() {
		return packetClientStatus;
	}

	public void setPacketClientStatus(String packetClientStatus) {
		this.packetClientStatus = packetClientStatus;
	}

	public String getPacketServerStatus() {
		return packetServerStatus;
	}

	public void setPacketServerStatus(String packetServerStatus) {
		this.packetServerStatus = packetServerStatus;
	}

	public String getPacketPath() {
		return packetPath;
	}

	public void setPacketPath(String packetPath) {
		this.packetPath = packetPath;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public String getClientStatusComments() {
		return clientStatusComments;
	}

	public void setClientStatusComments(String clientStatusComments) {
		this.clientStatusComments = clientStatusComments;
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
		return packetStatus;
	}

	public void setPacketStatus(String packetStatus) {
		this.packetStatus = packetStatus;
	}
	
	
}

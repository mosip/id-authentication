package io.mosip.registration.dto;

import javafx.beans.property.BooleanProperty;

public class PacketStatusDTO {

	private String sourcePath;
	private String fileName;
	private String packetClientStatus;
	private String packetServerStatus;
	private BooleanProperty status;
	private String packetPath;

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

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

	public BooleanProperty getStatus() {
		return status;
	}

	public void setStatus(BooleanProperty status) {
		this.status = status;
	}



}

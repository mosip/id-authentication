package io.mosip.registration.dto;

import java.util.Date;

public class PreRegistrationDTO {
	
	private String preRegId;
	private byte[] encryptedPacket;
	private String packetPath;
	private String symmetricKey;
	public String getPreRegId() {
		return preRegId;
	}
	public void setPreRegId(String preRegId) {
		this.preRegId = preRegId;
	}
	public byte[] getEncryptedPacket() {
		return encryptedPacket;
	}
	public void setEncryptedPacket(byte[] encryptedPacket) {
		this.encryptedPacket = encryptedPacket;
	}
	public String getPacketPath() {
		return packetPath;
	}
	public void setPacketPath(String packetPath) {
		this.packetPath = packetPath;
	}
	public String getSymmetricKey() {
		return symmetricKey;
	}
	public void setSymmetricKey(String symmetricKey) {
		this.symmetricKey = symmetricKey;
	}
	
	
	

}

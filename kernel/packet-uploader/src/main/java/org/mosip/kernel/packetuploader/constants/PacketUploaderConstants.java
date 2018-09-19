package org.mosip.kernel.packetuploader.constants;

public enum PacketUploaderConstants {
	STR_STRICT_HOST_KEY_CHECKING("StrictHostKeyChecking","no"), 
	STR_SFTP("sftp"), 
	AUTHENTICATIONS("PreferredAuthentications","publickey,keyboard-interactive,password");
	
	
	
	private PacketUploaderConstants(String value) {
		this.setValue(value);
	}
	
	private PacketUploaderConstants(String key,String value) {
		this.setKey(key);
		this.setValue(value);
	}

	private String value;
	private String key;
	

	public String getKey() {
		return key;
	}

	private void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	private void setValue(String value) {
		this.value = value;
	}
}

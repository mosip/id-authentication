package io.mosip.registration.processor.packet.service.dto;

public class PacketGeneratorDto {

	private String reason;
	private String registrationType;
	private String uin;
	private String centerId;
	private String machineId;

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getRegistrationType() {
		return registrationType;
	}

	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}

	public String getCenterId() {
		return centerId;
	}

	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	public String getMachineId() {
		return machineId;
	}

	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

}

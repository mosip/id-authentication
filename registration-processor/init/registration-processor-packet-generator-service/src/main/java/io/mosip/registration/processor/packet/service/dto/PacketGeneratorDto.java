package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Sowmya The Class PacketGeneratorDto.
 */
@Data
public class PacketGeneratorDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1587235057041204701L;

	/** The reason. */
	private String reason;

	/** The registration type. */
	private String registrationType;

	/** The uin. */
	private String uin;

	/** The center id. */
	private String centerId;

	/** The machine id. */
	private String machineId;

	/**
	 * Gets the reason.
	 *
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Sets the reason.
	 *
	 * @param reason
	 *            the new reason
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * Gets the registration type.
	 *
	 * @return the registration type
	 */
	public String getRegistrationType() {
		return registrationType;
	}

	/**
	 * Sets the registration type.
	 *
	 * @param registrationType
	 *            the new registration type
	 */
	public void setRegistrationType(String registrationType) {
		this.registrationType = registrationType;
	}

	/**
	 * Gets the uin.
	 *
	 * @return the uin
	 */
	public String getUin() {
		return uin;
	}

	/**
	 * Sets the uin.
	 *
	 * @param uin
	 *            the new uin
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}

	/**
	 * Gets the center id.
	 *
	 * @return the center id
	 */
	public String getCenterId() {
		return centerId;
	}

	/**
	 * Sets the center id.
	 *
	 * @param centerId
	 *            the new center id
	 */
	public void setCenterId(String centerId) {
		this.centerId = centerId;
	}

	/**
	 * Gets the machine id.
	 *
	 * @return the machine id
	 */
	public String getMachineId() {
		return machineId;
	}

	/**
	 * Sets the machine id.
	 *
	 * @param machineId
	 *            the new machine id
	 */
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}

}

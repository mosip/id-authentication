package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The Class RegPacketStatusDTO.
 * 
 * @author Rishab
 */
@Data
public class RegPacketStatusDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2877422595155239292L;

	/** The packet id. */
	private String packetId;

	/** The status. */
	private String status;

	/**
	 * Instantiates a new reg packet status DTO.
	 *
	 * @param packetId
	 *            the packet id
	 * @param status
	 *            the status
	 */
	public RegPacketStatusDTO(String packetId, String status) {
		super();
		this.packetId = packetId;
		this.status = status;
	}

	/**
	 * Gets the packet id.
	 *
	 * @return the packet id
	 */
	public String getPacketId() {
		return packetId;
	}

	/**
	 * Sets the packet id.
	 *
	 * @param packetId
	 *            the new packet id
	 */
	public void setPacketId(String packetId) {
		this.packetId = packetId;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the new status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

}

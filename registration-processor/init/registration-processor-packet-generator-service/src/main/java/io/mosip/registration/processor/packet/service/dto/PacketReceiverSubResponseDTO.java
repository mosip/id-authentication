package io.mosip.registration.processor.packet.service.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Instantiates a new response DTO.
 * @author Rishabh Keshari
 */
@Data
public class PacketReceiverSubResponseDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3501660956959221378L;
	
	/** The status. */
	private String status;
	
}

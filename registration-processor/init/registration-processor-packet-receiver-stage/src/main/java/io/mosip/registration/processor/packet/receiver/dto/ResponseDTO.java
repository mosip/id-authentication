package io.mosip.registration.processor.packet.receiver.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Instantiates a new response DTO.
 * @author Rishabh Keshari
 */
@Data
public class ResponseDTO implements Serializable{

	private static final long serialVersionUID = -6640160058127995099L;
	/** The status. */
	private String status;
	
}

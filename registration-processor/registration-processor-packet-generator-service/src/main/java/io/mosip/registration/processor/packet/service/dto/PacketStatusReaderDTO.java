package io.mosip.registration.processor.packet.service.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Gets the request.
 *
 * @return the request
 * @author Rishabh Keshari
 */
@Getter

/**
 * Sets the request.
 *
 * @param request
 *            the new request
 */
@Setter

/*
 * (non-Javadoc)
 * 
 * @see java.lang.Object#toString()
 */
@ToString
public class PacketStatusReaderDTO {

	/** The id. */
	private String id;

	/** The version. */
	private String version;

	/** The request timestamp. */
	private String requestTimestamp;

	/** The request. */
	private List<RegistrationIdDTO> request;

}

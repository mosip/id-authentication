package io.mosip.registration.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The DTO Class PacketStatusReaderDTO.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
public class PacketStatusReaderDTO {

	private String id;
	private String version;
	private String requesttime;
	private List<RegistrationIdDTO> request;
	
	
}

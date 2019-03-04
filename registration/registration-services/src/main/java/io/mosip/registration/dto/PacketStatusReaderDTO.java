package io.mosip.registration.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PacketStatusReaderDTO {

	private String id;
	private String version;
	private String requestTimestamp;
	private List<RegistrationIdDTO> request;
	
	
}

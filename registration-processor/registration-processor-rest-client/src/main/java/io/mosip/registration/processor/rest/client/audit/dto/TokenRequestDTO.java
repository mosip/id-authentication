package io.mosip.registration.processor.rest.client.audit.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class TokenRequestDTO {
	public String id;
	public Metadata metadata;
	public Request request;
	public String requesttime;
	public String version;
}

package io.mosip.resident.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class ClientIdSecretKeyRequestDto {
	public String clientId;
	public String secretKey;
	public String appId;
}

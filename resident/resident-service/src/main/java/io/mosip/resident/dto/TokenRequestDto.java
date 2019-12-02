package io.mosip.resident.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class TokenRequestDto extends BaseRequestDTO {

	private ClientIdSecretKeyRequestDto request;
}

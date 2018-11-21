package io.kernel.core.idrepo.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class IdRequestDTO extends BaseIdRequestResponseDTO {
	private String uin;
	private String status;
	private String registrationId;
	private Object request;
}

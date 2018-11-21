package io.kernel.core.idrepo.dto;

import lombok.Data;

@Data
public class BaseIdRequestResponseDTO {
	private String id;
	private String ver;
	private String timestamp;
}

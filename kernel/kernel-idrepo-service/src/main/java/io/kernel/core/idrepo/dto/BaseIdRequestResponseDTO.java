package io.kernel.core.idrepo.dto;

import java.util.Date;

import lombok.Data;

@Data
public class BaseIdRequestResponseDTO {
	private String id;
	private String ver;
	private Date timestamp;
}

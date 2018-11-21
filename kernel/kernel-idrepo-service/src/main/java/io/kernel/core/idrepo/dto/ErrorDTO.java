package io.kernel.core.idrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {
	
	private String errCode;
	private String errMessage;
}

package io.mosip.authentication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SignatureStatusDto
 * @author Nagarjuna
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class SignatureStatusDto {

	private String status;
	
	private String payload;
}

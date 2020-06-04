package io.mosip.authentication.common.service.integration.dto;

import io.mosip.authentication.core.indauth.dto.BaseAuthRequestDTO;
import io.mosip.kernel.signature.dto.TimestampRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * signature-validate-Request model
 *  
 * @author Nagarjuna
 *
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TimestampRequestDTO extends BaseAuthRequestDTO{
  
	/**
	 * validate request model
	 */
	private TimestampRequestDto request;
}

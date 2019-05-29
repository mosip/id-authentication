package io.mosip.registration.processor.stages.uingenerator.dto;

import java.util.List;

import io.mosip.registration.processor.core.common.rest.dto.ErrorDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Instantiates a new uin gen response dto.
 */
@Data
@Setter
@Getter
public class UinGenResponseDto {
	    
		/** The id. */
		private String id;
		
		/** The version. */
		private String version;
		
		/** The responsetime. */
		private String responsetime;
		
		/** The metadata. */
		private String metadata;
		
		/** The response. */
		private UinResponseDto response;
		/** The err. */
		private List<ErrorDTO> errors;


}

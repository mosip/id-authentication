package io.mosip.idrepository.vid.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.mosip.kernel.core.exception.ServiceError;
import lombok.Data;

/**
 * 
 * @author Prem Kumar
 *
 */
@Data
public class VidResponseDTO {
	
	private String id;
	
	private String version;
	
	private LocalDateTime responseTime;
	
	private List<ServiceError> errors;
	
	private ResponseDto response;
}

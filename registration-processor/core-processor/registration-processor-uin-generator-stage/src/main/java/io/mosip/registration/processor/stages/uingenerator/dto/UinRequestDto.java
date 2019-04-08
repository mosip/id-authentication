/**
 * 
 */
package io.mosip.registration.processor.stages.uingenerator.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author M1047487
 *
 */
@Data
@ToString
public class UinRequestDto {
	
	private String id;
	
	private String version;
	
	private Object metadata;
	
	private String requesttime;
	
	private UinResponseDto request;

}

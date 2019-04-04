package io.mosip.registration.processor.stages.uingenerator.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UinGenResponseDto {
	
		String id;
		String version;
		String responsetime;
		String metadata;
		UinResponseDto response;
		String errors;


}

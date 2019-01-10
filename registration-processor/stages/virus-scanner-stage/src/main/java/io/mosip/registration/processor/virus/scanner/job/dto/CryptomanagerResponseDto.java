package io.mosip.registration.processor.virus.scanner.job.dto;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Girish Yarru
 *
 */
@Data
public class CryptomanagerResponseDto {
	private String data;
	private String timestamp;
	private String status;
	private List<ErrorDto> errors;

}

package io.mosip.registration.processor.virus.scanner.job.dto;

import java.time.LocalDateTime;

import lombok.Data;
/**
 * 
 * @author Girish Yarru
 *
 */
@Data
public class CryptomanagerRequestDto {
	private String applicationId;
	private String referenceId;
	private LocalDateTime timeStamp;
	private String data;

}

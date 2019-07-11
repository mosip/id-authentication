package io.mosip.registration.processor.core.digital.signature.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class SignResponseDto {

	/**
	 * encrypted data
	 */
	private String signature;

	/**
	 * response time.
	 */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime timestamp;
}

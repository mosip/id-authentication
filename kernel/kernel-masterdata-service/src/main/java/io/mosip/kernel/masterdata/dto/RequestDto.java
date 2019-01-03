package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Bal Vikash Sharma
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDto<T> {

	private String id;
	private String ver;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime timestamp;
	@NotBlank
	@Valid
	private T request;

}

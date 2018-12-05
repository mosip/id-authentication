package io.mosip.kernel.synchandler.dto;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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
	private LocalDateTime timestamp;
	@NotNull
	@Valid
	private T request; 

} 
 

package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegCenterMachineUserReqDto<T> {

	private String id;
	private String ver;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime timestamp;
	@NotNull
	@Valid
	private T[] request;

}

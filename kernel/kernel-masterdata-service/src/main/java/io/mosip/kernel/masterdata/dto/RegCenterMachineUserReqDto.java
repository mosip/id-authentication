package io.mosip.kernel.masterdata.dto;

import java.time.LocalDateTime;
import java.util.List;

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
	private String version;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime requesttime;
	private Object metadata;
	@Valid
	@NotNull
	private List<T> request;
}

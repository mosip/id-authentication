package io.mosip.registration.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * The Class RegCenterMachineUserReqDto.
 * 
 * @author Brahmananda reddy
 *
 *
 * @param <T> the generic type
 */
@Data
public class RegCenterMachineUserReqDto<T> {
	private String id;
	private String version;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime requesttime;
	private List<T> request;

}

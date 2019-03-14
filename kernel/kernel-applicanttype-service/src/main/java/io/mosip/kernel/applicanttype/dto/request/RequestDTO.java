package io.mosip.kernel.applicanttype.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.mosip.kernel.applicanttype.dto.KeyValues;
import lombok.Data;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@Data
public class RequestDTO {
	private String id;
	private String ver;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime requestTime;
	@NotNull
	private List<KeyValues<String, Object>> attributes;

}

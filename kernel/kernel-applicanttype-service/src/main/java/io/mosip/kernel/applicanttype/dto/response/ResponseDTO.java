package io.mosip.kernel.applicanttype.dto.response;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 
 * @author Bal Vikash Sharma
 *
 */
@Data
public class ResponseDTO {


	private String id;
	private String ver;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime timestamp;
	@NotNull
	private ApplicantTypeCodeDTO response;

}

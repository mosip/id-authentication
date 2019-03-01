package io.mosip.kernel.applicanttype.dto.request;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class RequestDTO implements Serializable {

	private static final long serialVersionUID = -7906333665452736312L;

	private String id;
	private String ver;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime timestamp;
	@NotNull
	private KeyValues request;

}

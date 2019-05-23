package io.mosip.registration.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The DTO Class MainResponseDTO.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class MainResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;

	private List<PreRegistrationExceptionJSONInfoDTO> errors;

	private boolean status;

	@JsonProperty("responsetime")
	private String resTime;

	private String id;

	private String version;

	private T response;

}

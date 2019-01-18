package io.mosip.registration.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MainResponseDTO<T> implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3384945682672832638L;

	/** The error details. */
	private PreRegistrationExceptionJSONInfoDTO err;

	private boolean status;

	private String resTime;

	private T response;

}

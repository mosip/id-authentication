package io.mosip.registration.processor.printing.api.dto;

import java.io.Serializable;

import io.mosip.registration.processor.core.constant.IdType;
import lombok.Data;

/**
 * Instantiates a new request DTO.
 * 
 * @author M1048358 Alok
 */
@Data
public class RequestDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The idtype. */
	private IdType idtype;

	/** The id value. */
	private String idValue;
}

package io.mosip.registration.processor.packet.service.dto.demographic;

import java.io.Serializable;

import lombok.Data;

/**
 * This class will contains the language code and value for the field
 *
 * @author Sowmya
 * @since 1.0.0
 */
@Data
public class ValuesDTO implements Serializable {

	/** The language. */
	private String language;

	/** The value. */
	private String value;

}

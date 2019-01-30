package io.mosip.registration.processor.core.packet.dto.idjson;

import com.fasterxml.jackson.annotation.JsonIgnore;


import lombok.Getter;
import lombok.Setter;

/**
 * This class used to capture the Documents' details of the Individual
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 */
@Getter
@Setter
public class DocumentDetailsDTO{

	@JsonIgnore
	private byte[] document;
	protected String value;
	protected String type;
	@JsonIgnore
	protected String owner;
	protected String format;

}

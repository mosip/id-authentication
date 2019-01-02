package io.mosip.registration.processor.core.notification.template.generator.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * The class for ResponseDto.
 * 
 * @author Alok
 * @since 1.0.0
 */
@Data
public class ResponseDto implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The status.
	 */
	private String status;
}

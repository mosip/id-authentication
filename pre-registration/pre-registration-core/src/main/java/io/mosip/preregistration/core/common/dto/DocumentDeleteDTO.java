/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO class is used to define Document Id and message.
 * 
 * @author Tapaswini Behera
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class DocumentDeleteDTO implements Serializable {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;

	/**
	 * Document Id
	 */
	private String documnet_Id;

	/**
	 * Response Message
	 */
	private String resMsg;

}

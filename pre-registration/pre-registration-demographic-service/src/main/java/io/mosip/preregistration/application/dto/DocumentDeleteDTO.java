/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.application.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This DTO class is used to define Document Id and message.
 * 
 * @author Tapaswini Bahera
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class DocumentDeleteDTO implements Serializable {

	private static final long serialVersionUID = 7070542323407937205L;

	private String documnet_Id;
	private String resMsg;

}

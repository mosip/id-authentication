/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.document.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the values for copy document.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DocumentCopyResponseDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;
	
	/**
	 * source PreRegistration Id
	 */
	@ApiModelProperty(value = "Source PreReg ID", position = 1)
	private String sourcePreRegId;
	/**
	 * source Document Id 
	 */

	@ApiModelProperty(value = "Source Document ID", position = 2)
	private String sourceDocumentId;
	/**
	 * destination PreRegistration Id
	 */
	@ApiModelProperty(value = "Destination PreReg ID", position = 3)
	private String destPreRegId;
	
	/**
	 * destination Document Id
	 */
	@ApiModelProperty(value = "Destination Document ID", position = 4)
	private String destDocumentId;

}

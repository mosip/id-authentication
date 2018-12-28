/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.documents.dto;

import java.io.Serializable;

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
public class DocumentCopyDTO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;
	
	/**
	 * source PreRegistration Id
	 */
	private String sourcePreRegId;
	/**
	 * source Document Id 
	 */
	private String sourceDocumnetId;
	/**
	 * destination PreRegistration Id
	 */
	private String destPreRegId;
	/**
	 * destination Document Id
	 */
	private String destDocumnetId;

}

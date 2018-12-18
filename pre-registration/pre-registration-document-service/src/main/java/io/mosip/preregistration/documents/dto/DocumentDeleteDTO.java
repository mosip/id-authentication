package io.mosip.preregistration.documents.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DocumentDeleteDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070542323407937205L;
	
	private String documnet_Id;
	private String resMsg;

}

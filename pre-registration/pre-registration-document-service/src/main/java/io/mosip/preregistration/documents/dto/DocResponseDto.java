package io.mosip.preregistration.documents.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class DocResponseDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7070542323407937205L;
	
	
	private String preRegsitrationId;
	private String documnetId;
	private String documentName;
	private String documentCat;
	private String documentType;
	private String resMsg;

}

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
	
	
	private String pre_regsitration_id;
	private String documnet_Id;
	private String document_Name;
	private String document_Cat;
	private String document_Type;
	private String resMsg;

}

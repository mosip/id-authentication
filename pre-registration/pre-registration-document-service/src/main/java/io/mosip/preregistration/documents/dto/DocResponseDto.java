package io.mosip.preregistration.documents.dto;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
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
	
	private String resMsg;

}

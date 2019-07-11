package io.mosip.preregistration.core.common.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This DTO class is used to define the list of document details.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DocumentsMetaData implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 7070542323407937205L;
	
	List<DocumentMultipartResponseDTO> documentsMetaData;
}

package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DocumentTypeDto;
import lombok.Data;
/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 */

@Data
public class DocumentTypeResponseDto {
	private List<DocumentTypeDto> documenttypes;

}

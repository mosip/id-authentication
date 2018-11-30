package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DocumentTypeData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Uday Kumar
 * @since 1.0.0
 *
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidDocumentTypeResponseDto {
	private List<DocumentTypeData> documents;

}

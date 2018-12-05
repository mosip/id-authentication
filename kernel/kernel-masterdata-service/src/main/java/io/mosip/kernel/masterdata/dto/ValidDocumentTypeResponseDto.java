package io.mosip.kernel.synchandler.dto;

import java.util.List;

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
	private List<DocumentTypeDto> documents;

}

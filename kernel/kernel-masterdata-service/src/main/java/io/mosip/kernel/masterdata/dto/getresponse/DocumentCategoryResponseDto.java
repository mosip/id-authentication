package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.DocumentCategoryData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author Neha
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentCategoryResponseDto {
	private List<DocumentCategoryData> documentcategories;
}

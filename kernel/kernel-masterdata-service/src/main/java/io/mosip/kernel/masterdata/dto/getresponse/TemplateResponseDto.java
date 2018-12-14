package io.mosip.kernel.masterdata.dto.getresponse;

import java.util.List;

import io.mosip.kernel.masterdata.dto.TemplateDto;
import lombok.Data;

/**
 * 
 * @author Neha
 * @since 1.0.0
 */
@Data


public class TemplateResponseDto {
	private List<TemplateDto> templates;
}

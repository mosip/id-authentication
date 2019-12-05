package io.mosip.resident.dto;

import java.io.Serializable;
import java.util.List;

import io.mosip.kernel.core.http.ResponseWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateResponseDto extends ResponseWrapper<TemplateResponseDto> implements Serializable {
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private List<TemplateDto> templates;
}

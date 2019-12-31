package io.mosip.resident.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class TemplateResponseDto implements Serializable {
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private List<TemplateDto> templates;
}

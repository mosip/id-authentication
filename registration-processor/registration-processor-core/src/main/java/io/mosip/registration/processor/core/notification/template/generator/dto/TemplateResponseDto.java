package io.mosip.registration.processor.core.notification.template.generator.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author Alok
 * @since 1.0.0
 */
@Data


public class TemplateResponseDto implements Serializable {
	/**
	 * Default serial version
	 */
	private static final long serialVersionUID = 1L;
	private List<TemplateDto> templates;
}

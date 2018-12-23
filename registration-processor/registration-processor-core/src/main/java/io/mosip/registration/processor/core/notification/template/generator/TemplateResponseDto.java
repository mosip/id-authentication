package io.mosip.registration.processor.core.notification.template.generator;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 
 * @author Alok Ranjan
 * @since 1.0.0
 */

@Data
public class TemplateResponseDto implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<TemplateDto> templates;
}

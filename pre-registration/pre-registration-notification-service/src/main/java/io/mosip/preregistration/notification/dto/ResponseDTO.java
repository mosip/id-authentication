package io.mosip.preregistration.notification.dto;

import java.util.List;

import lombok.Data;

/**
 * The DTO for template response.
 * 
 * @author Sanober Noor
 * @since 1.0.0
 */
@Data
public class ResponseDTO {
	/**
	 * The list of {@link TemplateResponseDTO}.
	 */
	List<TemplateResponseDTO> templates;
}

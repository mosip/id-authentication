package io.mosip.registration.processor.request.handler.service.dto;

import java.io.Serializable;

import javax.validation.Valid;

import io.mosip.registration.processor.core.common.rest.dto.BaseRestRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
/**
 * 
 * @author Girish Yarru
 * @since 1.0
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResidentUpdateRequestDto extends BaseRestRequestDTO implements Serializable{

	private static final long serialVersionUID = 7137353102551643957L;
	@Valid
	private ResidentUpdateDto request;

}

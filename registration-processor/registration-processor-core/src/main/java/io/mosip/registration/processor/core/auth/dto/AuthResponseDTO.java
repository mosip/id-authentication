package io.mosip.registration.processor.core.auth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@code AuthResponseDTO} is used for collect response from
 * core-kernel.Core-kernel get request from {@code AuthRequestDTO} and perform
 * operation.In result send
 * {@link AuthResponseDTO#info}
 * 
 * 
 * 
 * @author Rakesh Roshan
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthResponseDTO extends BaseAuthResponseDTO {

	private AuthResponseInfo info;

}

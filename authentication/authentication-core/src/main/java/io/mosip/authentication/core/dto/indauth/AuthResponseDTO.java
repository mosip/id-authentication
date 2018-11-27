package io.mosip.authentication.core.dto.indauth;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * {@code AuthResponseDTO} is used for collect response from
 * core-kernel.Core-kernel get request from {@code AuthRequestDTO} and perform
 * operation.In result send
 * {@link AuthResponseDTO#status},{@link AuthResponseDTO#errorCode},
 * {@link AuthResponseDTO#txnID},
 * {@link AuthResponseDTO#responseTime},{@link AuthResponseDTO#inf	o}
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

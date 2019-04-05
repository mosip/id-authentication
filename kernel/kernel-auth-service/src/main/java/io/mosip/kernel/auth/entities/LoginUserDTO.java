package io.mosip.kernel.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoginUserDTO extends BaseRequestResponseDTO {

	private LoginUser request;
}

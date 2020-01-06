/**
 * 
 */
package io.mosip.kernel.auth.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserOtpDto extends BaseRequestResponseDto {

	private UserOtp request;

}

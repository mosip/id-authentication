/**
 * 
 */
package io.mosip.kernel.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author M1049825
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserOtpDto extends BaseRequestResponseDTO {

	private UserOtp request;

}

/**
 * 
 */
package io.mosip.kernel.auth.entities.otp;

import io.mosip.kernel.auth.entities.BaseRequestResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author M1049825
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OtpUserDto extends BaseRequestResponseDTO {

	private OtpUser request;

}

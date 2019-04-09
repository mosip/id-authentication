/**
 * 
 */
package io.mosip.kernel.auth.entities.otp;

import io.mosip.kernel.auth.entities.BaseRequestResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OtpUserDto extends BaseRequestResponseDTO {

	private OtpUser request;

}

/**
 * 
 */
package io.mosip.kernel.auth.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Ramadurai Pandian
 *
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientSecretDto extends BaseRequestResponseDTO {

	private ClientSecret request;

}

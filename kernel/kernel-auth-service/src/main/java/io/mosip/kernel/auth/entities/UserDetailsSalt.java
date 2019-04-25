/**
 * 
 */
package io.mosip.kernel.auth.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsSalt {
	
	private String userId;
	private String salt;

}

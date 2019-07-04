/**
 * 
 */
package io.mosip.kernel.auth.dto;

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
public class MosipUserSalt {
	
	private String userId;
	private String salt;

}

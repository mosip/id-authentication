/**
 * 
 */
package io.mosip.kernel.auth.dto;

import lombok.Data;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
public class LoginUser {

	private String userName;
	private String password;
	private String appId;

}

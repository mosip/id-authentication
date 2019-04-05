/**
 * 
 */
package io.mosip.kernel.auth.entities;

import lombok.Data;

/**
 * @author M1049825
 *
 */
@Data
public class LoginUser {

	private String userName;
	private String password;
	private String appId;

}

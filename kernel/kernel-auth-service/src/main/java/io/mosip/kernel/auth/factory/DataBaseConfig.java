/**
 * 
 */
package io.mosip.kernel.auth.factory;

import lombok.Data;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
public class DataBaseConfig {

	private String url;
	private String port;
	private String username;
	private String password;
	private String schemas;
	private String driverName;
}

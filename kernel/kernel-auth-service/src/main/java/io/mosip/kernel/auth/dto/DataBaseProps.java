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
public class DataBaseProps {
	private String url;
	private String port;
	private String username;
	private String password;
	private String schemas;
	private String driverName;
}

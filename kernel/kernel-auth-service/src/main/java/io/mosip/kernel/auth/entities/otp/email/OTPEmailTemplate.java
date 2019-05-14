/**
 * 
 */
package io.mosip.kernel.auth.entities.otp.email;

import lombok.Data;

/**
 * @author Ramadurai Pandian
 *
 */
@Data
public class OTPEmailTemplate {
	
	private String emailSubject;
	
	private String emailContent;
	
	private String emailTo;

}

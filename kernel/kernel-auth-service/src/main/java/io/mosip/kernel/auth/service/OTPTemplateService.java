/**
 * 
 */
package io.mosip.kernel.auth.service;

import io.mosip.kernel.auth.entities.MosipUserDto;

/**
 * @author Ramadurai Pandian
 *
 */
public interface OTPTemplateService {
	
	String getTemplatesBasedOnAppId(MosipUserDto mosipUserDto, String channel);

}

/**
 * 
 */
package io.mosip.kernel.auth.service;

/**
 * @author Ramadurai Pandian
 *
 */
public interface TokenGenerationService {
	
	String getInternalTokenGenerationService() throws Exception;

	String getUINBasedToken() throws Exception;

}

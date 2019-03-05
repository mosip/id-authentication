/**
 * 
 */
package io.mosip.kernel.auth.adapter;

import java.io.IOException;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * @author M1049825
 *
 */
public class AuthErrorHandler extends DefaultResponseErrorHandler{

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		
		
	}

}

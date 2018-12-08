package io.mosip.authentication.service.filter;

import java.util.Map;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.springframework.stereotype.Component;

import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;

/**
 * The Class IdAuthFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdAuthFilter extends BaseAuthFilter {

	/** The Constant REQUEST. */
	private static final String REQUEST = "request";

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#setTxnId(java.util.Map, java.util.Map)
	 */
	@Override
	protected Map<String, Object> setTxnId(Map<String, Object> requestBody,
			Map<String, Object> responseBody) {
		responseBody.replace("txnID", requestBody.get("txnID"));
		return responseBody;
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		
	}
	

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#decodedRequest(java.util.Map)
	 */
	@Override
	protected Map<String, Object> decodedRequest(
			Map<String, Object> requestBody)
			throws IdAuthenticationAppException {
		try {
			requestBody.replace(REQUEST,
					decode((String) requestBody.get(REQUEST)));
			Map<String, Object> request = keyManager.requestData(requestBody, env, decryptor, mapper);
			requestBody.replace(REQUEST, request);
			return requestBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}

	/* (non-Javadoc)
	 * @see io.mosip.authentication.service.filter.BaseAuthFilter#encodedResponse(java.util.Map)
	 */
	@Override
	protected Map<String, Object> encodedResponse(
			Map<String, Object> responseBody)
			throws IdAuthenticationAppException {
		try {
			responseBody.replace(REQUEST,
					encode((String) responseBody.get(REQUEST)));
			return responseBody;
		} catch (ClassCastException e) {
			throw new IdAuthenticationAppException(
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorCode(),
					IdAuthenticationErrorConstants.INVALID_AUTH_REQUEST
							.getErrorMessage());
		}
	}
	
	/*public byte[] fileReader(String filename) throws IOException {
		String localpath = env.getProperty(FILEPATH);
		Object[] homedirectory = new Object[] { System.getProperty("user.home") + File.separator };
		String finalpath = MessageFormat.format(localpath, homedirectory);
		File fileInfo = new File(finalpath + File.separator + filename);
		File parentFile = fileInfo.getParentFile();
		byte[] output = null;
		if (parentFile.exists()) {
			output = Files.readAllBytes(fileInfo.toPath());
		}
		return output;
	}*/

}
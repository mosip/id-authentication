package io.mosip.authentication.common.service.filter;

/**
 * The Class ExternalAuthFilter.
 * @author Loganathan Sekar
 */
public class ExternalAuthFilter extends IdAuthFilter {
	
	/** The Constant AUTH. */
	private static final String AUTH = "auth";

	/**
	 * Fetch id.
	 *
	 * @param requestWrapper the request wrapper
	 * @param attribute the attribute
	 * @return the string
	 */
	@Override
	protected String fetchId(ResettableStreamHttpServletRequest requestWrapper, String attribute) {
		return attribute + AUTH;
	}
}

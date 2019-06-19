package io.mosip.idrepository.vid.httpfilter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.httpfilter.BaseIdRepoFilter;

/**
 * The Class IdRepoFilter.
 *
 * @author Manoj SP
 */
@Component
public final class IdRepoFilter extends BaseIdRepoFilter {

	/**
	 * Builds the response.
	 *
	 * @param request the request
	 * @return the string
	 */
	@Override
	protected final String buildResponse(HttpServletRequest request) {
		return null;
	}

}

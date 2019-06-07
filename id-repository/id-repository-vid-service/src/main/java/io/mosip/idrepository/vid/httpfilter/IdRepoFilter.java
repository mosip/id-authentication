package io.mosip.idrepository.vid.httpfilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.filter.BaseIdRepoFilter;

/**
 * The Class IdRepoFilter.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoFilter extends BaseIdRepoFilter {

	@Override
	protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		filterChain.doFilter(request, response);
	}

}

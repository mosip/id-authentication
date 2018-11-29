package io.mosip.kernel.synchandler.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import io.mosip.kernel.synchandler.entity.AuthUser;
import io.mosip.kernel.synchandler.entity.AuthenticationToken;

public class SecurityFilter extends GenericFilterBean {

	private static final Logger SFLOGGER = LoggerFactory.getLogger(SecurityFilter.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
			throws ServletException, IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		// TO DO: Security check and context set

		// ********Mock Context*********
		try {
			List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("1001,1002");
			AuthUser authUser = new AuthUser("defaultadmin@mosip.io", "Mosip Admin", "[PROTECTED]", authorities);
			Authentication authN = new AuthenticationToken(authUser, null, authorities);
			SecurityContextHolder.getContext().setAuthentication(authN);
		} catch (Exception e) {
			SFLOGGER.error(e.getMessage());
		}
		// *************************
		filterChain.doFilter(request, response);
	}
}

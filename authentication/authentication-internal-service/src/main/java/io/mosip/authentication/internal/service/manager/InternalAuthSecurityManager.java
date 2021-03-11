package io.mosip.authentication.internal.service.manager;

import java.util.Objects;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;

/**
 * 
 * @author Arun Bose
 * 
 * The Class IdAuthSecurityManager.
 */
@Component
public class InternalAuthSecurityManager extends IdAuthSecurityManager {

	/**
	 * provides the user id.
	 *
	 * @return the user
	 */
	@Override
	public String getUser() {
		if (Objects.nonNull(SecurityContextHolder.getContext())
				&& Objects.nonNull(SecurityContextHolder.getContext().getAuthentication())
				&& Objects.nonNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
				&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails) {	
			return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
					.getUsername();
		} else {
			return "";
		}
	}
	
}

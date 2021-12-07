package io.mosip.authentication.common.service.filter;

import org.springframework.stereotype.Component;

@Component
public class InternalOtpFilter extends DefaultInternalFilter {
	
	protected boolean needStoreAuthTransaction() {
		return true;
	}
	
	protected boolean needStoreAnonymousProfile() {
		return false;
	}

}

package io.mosip.authentication.common.service.filter;


public class InternalOtpFilter extends DefaultInternalFilter {
	
	protected boolean needStoreAuthTransaction() {
		return true;
	}
	
	protected boolean needStoreAnonymousProfile() {
		return false;
	}

}

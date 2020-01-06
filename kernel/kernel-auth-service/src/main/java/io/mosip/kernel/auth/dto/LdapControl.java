package io.mosip.kernel.auth.dto;

import javax.naming.ldap.Control;

public class LdapControl implements Control {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8917803695932582767L;

	public byte[] getEncodedValue() {
		return null;
	}

	public String getID() {
		return "1.3.6.1.4.1.42.2.27.8.5.1";
	}

	public boolean isCritical() {
		return true;
	}

	public Control[] getControls() {
		return new Control[] { this };
	}
}

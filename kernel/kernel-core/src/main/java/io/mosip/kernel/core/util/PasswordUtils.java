package io.mosip.kernel.core.util;

import org.apache.directory.api.ldap.model.password.PasswordUtil;

public class PasswordUtils {

	
	public static void compareCredential(String plainText,String storedCredential) {
		
		boolean isValid=PasswordUtil.compareCredentials(plainText.getBytes(), CryptoUtil.decodeBase64(storedCredential));
		System.out.println(isValid);
	}
}

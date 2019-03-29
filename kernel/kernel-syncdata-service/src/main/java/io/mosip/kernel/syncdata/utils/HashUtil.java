package io.mosip.kernel.syncdata.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

import io.mosip.kernel.syncdata.constant.HashAlgoConstant;

@Component
public class HashUtil {

	public String hashData(String response) {

		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance(HashAlgoConstant.SHA_256_HASH_ALGO.getAlgoName());
		} catch (NoSuchAlgorithmException e) {
			// throw appropriate error
			e.printStackTrace();
		}
		byte[] encodedhash = null;
		if (digest != null) {
			encodedhash = digest.digest(response.getBytes(StandardCharsets.UTF_8));
		}
		String hashText = bytesToHex(encodedhash);
		return hashText;

	}

	private String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

}

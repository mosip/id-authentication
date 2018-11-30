/**
 * 
 */
package io.mosip.kernel.keymanagerservice.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import io.mosip.kernel.keymanagerservice.entity.KeyAlias;

/**
 * @author Dharmesh Khandelwal
 * @since 1.0.0
 *
 */
@Component
public class KeymanagerUtil {

	public KeyAlias setMetaData(KeyAlias entity) {
		String contextUser = "defaultadmin@mosip.io";
		LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
		entity.setCreatedBy(contextUser);
		entity.setCreatedtimes(time);
		entity.setIsDeleted(false);
		return entity;
	}

	public String encodeBase64(byte[] binaryData) {
		return Base64.encodeBase64URLSafeString(binaryData);
	}

	public byte[] decodeBase64(String base64String) {
		return Base64.decodeBase64(base64String);
	}

}

package io.mosip.kernel.crypto.utils;

import org.apache.commons.codec.binary.Base64;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.crypto.dto.CryptoRequestDto;
import io.mosip.kernel.crypto.dto.KeyManagerSymmetricKeyRequestDto;


/**Custom converter for KeyManagerSymmetricKeyRequestDto and CryptoRequestDto
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class KeyManagerSymmetricKeyConverter implements DataConverter<CryptoRequestDto, KeyManagerSymmetricKeyRequestDto> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataConverter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void convert(CryptoRequestDto source, KeyManagerSymmetricKeyRequestDto destination) {
		destination.setApplicationId(source.getApplicationId());
		destination.setReferenceId(source.getReferenceId());
		destination.setTimeStamp(source.getTimeStamp());
		destination.setEncryptedSymmetricKey(source.getData());
	}

	

}

package io.mosip.kernel.crypto.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.crypto.dto.CryptoRequestDto;
import io.mosip.kernel.crypto.dto.KeyManagerPublicKeyRequestDto;

/**Custom converter for KeyManagerPublicKeyRequestDto and CryptoRequestDto
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class KeyManagerPublicKeyConverter implements DataConverter<CryptoRequestDto, KeyManagerPublicKeyRequestDto> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataConverter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void convert(CryptoRequestDto source, KeyManagerPublicKeyRequestDto destination) {
		destination.setApplicationId(source.getApplicationId());
		destination.setMachineId(source.getMachineId());
		destination.setTimeStamp(source.getTimeStamp());
	}

	

}

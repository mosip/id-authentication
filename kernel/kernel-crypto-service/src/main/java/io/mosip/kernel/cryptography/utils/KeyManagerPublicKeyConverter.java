package io.mosip.kernel.cryptography.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.KeyManagerPublicKeyRequestDto;

/**Custom converter for KeyManagerPublicKeyRequestDto and CryptographyRequestDto
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class KeyManagerPublicKeyConverter implements DataConverter<CryptographyRequestDto, KeyManagerPublicKeyRequestDto> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataConverter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void convert(CryptographyRequestDto source, KeyManagerPublicKeyRequestDto destination) {
		destination.setApplicationId(source.getApplicationId());
		destination.setMachineId(source.getMachineId());
		destination.setTimeStamp(source.getTimeStamp());
	}

	

}

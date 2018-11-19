package io.mosip.kernel.cryptography.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.KeyManagerSymmetricKeyRequestDto;


/**Custom converter for KeyManagerSymmetricKeyRequestDto and CryptographyRequestDto
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class KeyManagerSymmetricKeyConverter implements DataConverter<CryptographyRequestDto, KeyManagerSymmetricKeyRequestDto> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataConverter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void convert(CryptographyRequestDto source, KeyManagerSymmetricKeyRequestDto destination) {
		destination.setApplicationId(source.getApplicationId());
		destination.setMachineId(source.getMachineId());
		destination.setTimeStamp(source.getTimeStamp());
		destination.setEncryptedSymmetricKey(source.getData());
	}

	

}

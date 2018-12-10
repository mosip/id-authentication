package io.mosip.kernel.cryptomanager.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerPublicKeyRequestDto;

/**Custom converter for {@link KeymanagerPublicKeyRequestDto} and {@link CryptomanagerRequestDto}
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class KeymanagerPublicKeyConverter implements DataConverter<CryptomanagerRequestDto, KeymanagerPublicKeyRequestDto> {

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.datamapper.spi.DataConverter#convert(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void convert(CryptomanagerRequestDto source, KeymanagerPublicKeyRequestDto destination) {
		destination.setApplicationId(source.getApplicationId());
		destination.setReferenceId(source.getReferenceId());
		destination.setTimeStamp(source.getTimeStamp());
	}

	

}

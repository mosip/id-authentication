package io.mosip.kernel.cryptomanager.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.cryptomanager.dto.CryptomanagerRequestDto;
import io.mosip.kernel.cryptomanager.dto.KeymanagerSymmetricKeyRequestDto;

/**
 * Custom converter for {@link KeymanagerSymmetricKeyRequestDto} and
 * {@link CryptomanagerRequestDto}
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
public class KeymanagerSymmetricKeyConverter
		implements DataConverter<CryptomanagerRequestDto, KeymanagerSymmetricKeyRequestDto> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.datamapper.spi.DataConverter#convert(java.lang.
	 * Object, java.lang.Object)
	 */
	@Override
	public void convert(CryptomanagerRequestDto source, KeymanagerSymmetricKeyRequestDto destination) {
		destination.setApplicationId(source.getApplicationId());
		destination.setReferenceId(source.getReferenceId());
		destination.setTimeStamp(source.getTimeStamp());
		destination.setEncryptedSymmetricKey(source.getData());
	}
}

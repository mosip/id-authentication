package io.mosip.kernel.cryptography.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.KeyManagerPublicKeyRequestDto;


public class KeyManagerPublicKeyConverter implements DataConverter<CryptographyRequestDto, KeyManagerPublicKeyRequestDto> {

	@Override
	public void convert(CryptographyRequestDto source, KeyManagerPublicKeyRequestDto destination) {
		destination.setAppId(source.getAppId());
		destination.setMachineId(source.getMachineId());
		destination.setTimeStamp(source.getTimeStamp());
	}

	

}

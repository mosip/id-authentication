package io.mosip.kernel.cryptography.utils;

import io.mosip.kernel.core.datamapper.spi.DataConverter;
import io.mosip.kernel.cryptography.dto.CryptographyRequestDto;
import io.mosip.kernel.cryptography.dto.KeyManagerSymmetricKeyRequestDto;


public class KeyManagerSymmetricKeyConverter implements DataConverter<CryptographyRequestDto, KeyManagerSymmetricKeyRequestDto> {

	@Override
	public void convert(CryptographyRequestDto source, KeyManagerSymmetricKeyRequestDto destination) {
		destination.setAppId(source.getAppId());
		destination.setMachineId(source.getMachineId());
		destination.setTimeStamp(source.getTimeStamp());
		destination.setData(source.getData());
	}

	

}

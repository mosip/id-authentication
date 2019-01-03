package io.mosip.registration.processor.bio.dedupe.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.abis.dto.AbisInsertRequestDto;
import io.mosip.registration.processor.abis.dto.AbisInsertResponceDto;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.packet.dto.RegAbisRefDto;
import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.packet.storage.service.impl.PacketInfoManagerImpl;

@RefreshScope
@Service
public class BioDedupeServiceImpl implements BioDedupeService {
	AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();

	/** The rest client service. */
	@Autowired
	RegistrationProcessorRestClientService<Object> restClientService;

	@Autowired
	PacketInfoManagerImpl packetInfoManagerImpl;

	@Value("${registration.processor.abis.url}")
	String url;

	@Override
	public String insertBiometrics(String registrationId) throws ApisResourceAccessException {

		String requestId = uuidGenerator();
		String referenceId = uuidGenerator();

		abisInsertRequestDto.setRequestId(requestId);
		abisInsertRequestDto.setReferenceId(referenceId);
		abisInsertRequestDto.setReferenceURL(url + registrationId);

		RegAbisRefDto regAbisRefDto = new RegAbisRefDto();
		regAbisRefDto.setAbis_ref_id(referenceId);
		regAbisRefDto.setReg_id(registrationId);

		packetInfoManagerImpl.saveAbisRef(regAbisRefDto);
		// chk
		AbisInsertResponceDto authResponseDTO = (AbisInsertResponceDto) restClientService
				.postApi(ApiName.BIODEDUPEINSERT, "", "", abisInsertRequestDto, AbisInsertResponceDto.class);

		return authResponseDTO.getReturnValue();

	}

	@Override
	public List<String> performDedupe(String registrationId) {
		String requestId = uuidGenerator();
		// Fetch the referenceId from Db
		// String referenceId =

		return null;
	}

	private String uuidGenerator() {
		return UUID.randomUUID().toString();
	}

}

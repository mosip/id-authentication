package io.mosip.registration.processor.bio.dedupe.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.mosip.registration.processor.core.spi.biodedupe.BioDedupeService;

@RefreshScope
@Service
public class BioDedupeImpl implements BioDedupeService {

	@Override
	public String insertBiometrics(String RegistrationId) {

		String InsertStatus = "failure";

		// AbisInsertRequestDto abisInsertRequestDto = new AbisInsertRequestDto();

		String requestId = UUIDgenerator();
		String referenceId = UUIDgenerator();

		return InsertStatus;

	}

	@Override
	public List<String> performDedupe(String RegistrationId) {
		// TODO Auto-generated method stub
		return null;
	}

	private String UUIDgenerator() {
		return UUID.randomUUID().toString();
	}

}

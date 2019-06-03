package io.mosip.registration.processor.packet.manager.idreposervice;

import java.io.IOException;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;

public interface IdRepoService {

	Number getUinFromIDRepo(String machedRegId, String regProcessorDemographicIdentity)
			throws IOException, ApisResourceAccessException;

}

package io.mosip.registration.processor.packet.manager.idreposervice;

import java.io.IOException;

import org.json.simple.JSONObject;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;

public interface IdRepoService {

	Number getUinByRid(String machedRegId, String regProcessorDemographicIdentity)
			throws IOException, ApisResourceAccessException;
	
	public Number findUinFromIdrepo(String uin, String regProcessorDemographicIdentity)
			throws IOException, ApisResourceAccessException;

	JSONObject getIdJsonFromIDRepo(String machedRegId, String regProcessorDemographicIdentity)
			throws IOException, ApisResourceAccessException;

}

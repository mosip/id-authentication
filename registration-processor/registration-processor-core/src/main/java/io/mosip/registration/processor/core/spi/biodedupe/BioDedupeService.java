package io.mosip.registration.processor.core.spi.biodedupe;

import java.util.List;

import io.mosip.registration.processor.core.exception.ApisResourceAccessException;

public interface BioDedupeService {

	public String insertBiometrics(String RegistrationId) throws ApisResourceAccessException;

	public List<String> performDedupe(String RegistrationId) throws ApisResourceAccessException;

}

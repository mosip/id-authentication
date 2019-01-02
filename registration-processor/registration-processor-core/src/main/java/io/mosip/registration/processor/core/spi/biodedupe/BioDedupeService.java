package io.mosip.registration.processor.core.spi.biodedupe;

import java.util.List;

public interface BioDedupeService {

	public String insertBiometrics(String RegistrationId);

	public List<String> performDedupe(String RegistrationId);

}

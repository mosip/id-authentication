package io.mosip.registration.device.fp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.machinezoo.sourceafis.FingerprintTemplate;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author SaravanaKumar G
 *
 */
@Component
public class FingerprintFacade {

	private static final Logger LOGGER = AppConfig.getLogger(FingerprintFacade.class);
	private List<MosipFingerprintProvider> fingerprintProviders;

	@Autowired
	private MosipFingerprintProvider fingerprintProvider;

	private byte[] isoTemplate;

	public byte[] getIsoTemplateFromMdm() {
		return isoTemplate;
	}

	/**
	 * provide the minutia of a finger.
	 *
	 * @return the minutia
	 */
	public String getMinutia() {
		return fingerprintProvider.getMinutia();
	}

	private String minitia;

	public String getMinitiaThroughMdm() {
		minitia = new FingerprintTemplate().convert(this.isoTemplate).serialize();
		return minitia;
	}

	/**
	 * Gets the iso template.
	 *
	 * @return the iso template
	 */
	public byte[] getIsoTemplate() {
		return fingerprintProvider.getIsoTemplate();
	}

	/**
	 * Gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return fingerprintProvider.getErrorMessage();

	}

		

	
	/**
	 * Assign all the Fingerprint providers which extends the
	 * MosipFingerprintProvider to the list.
	 *
	 * @param make
	 *            the make
	 * @return the fingerprint provider factory
	 */

	public MosipFingerprintProvider getFingerprintProviderFactory(String make) {
		for (MosipFingerprintProvider mosipFingerprintProvider : fingerprintProviders) {
			if (mosipFingerprintProvider.getClass().getName().toLowerCase().contains(make.toLowerCase())) {
				fingerprintProvider = mosipFingerprintProvider;
			}
		}
		return fingerprintProvider;
	}

	/**
	 * Sets the fingerprint providers.
	 *
	 * @param fingerprintProviders
	 *            the new fingerprint providers
	 */
	@Autowired
	public void setFingerprintProviders(List<MosipFingerprintProvider> fingerprintProviders) {
		this.fingerprintProviders = fingerprintProviders;
	}

	
}

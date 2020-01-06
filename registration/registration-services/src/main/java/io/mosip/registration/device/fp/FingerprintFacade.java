package io.mosip.registration.device.fp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.machinezoo.sourceafis.FingerprintTemplate;

/**
 * It takes a decision based on the input provider name and initialize the
 * respective implementation class and perform the required operation.
 * 
 * @author SaravanaKumar G
 *
 */
@Component
public class FingerprintFacade {

	private List<MosipFingerprintProvider> fingerprintProviders;

	@Autowired
	private MosipFingerprintProvider fingerprintProvider;

	private byte[] isoTemplate;

	public byte[] getIsoTemplateFromMdm() {
		return isoTemplate;
	}

	/**
	 * This method provides the minutia of a finger.
	 *
	 * @return the minutia
	 */
	public String getMinutia() {
		return fingerprintProvider.getMinutia();
	}

	private String minitia;

	/**
	 * This method gets the minitia through MDM.
	 *
	 * @return the minitia through MDM
	 */
	public String getMinitiaThroughMdm() {
		minitia = new FingerprintTemplate().convert(this.isoTemplate).serialize();
		return minitia;
	}

	/**
	 * This method gets the ISO Template.
	 *
	 * @return the ISO template in byte array format
	 */
	public byte[] getIsoTemplate() {
		return fingerprintProvider.getIsoTemplate();
	}

	/**
	 * This method gets the error message.
	 *
	 * @return the error message
	 */
	public String getErrorMessage() {
		return fingerprintProvider.getErrorMessage();

	}

	/**
	 * This method gets the Fingerprint Provider which extends the
	 * {@link MosipFingerprintProvider} containing specified name.
	 *
	 * @param make
	 *            the name which indicates device specific implementation
	 * @return the fingerprint provider factory containing specified name
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
	 * This method sets the list of available fingerprint providers.
	 *
	 * @param fingerprintProviders
	 *            the new list of {@link MosipFingerprintProvider}
	 */
	@Autowired
	public void setFingerprintProviders(List<MosipFingerprintProvider> fingerprintProviders) {
		this.fingerprintProviders = fingerprintProviders;
	}

}

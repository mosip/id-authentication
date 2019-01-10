package io.mosip.authentication.service.factory;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.BioInfo;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;
import io.mosip.authentication.service.impl.iris.CogentIrisProvider;
import io.mosip.authentication.service.impl.iris.MorphoIrisProvider;

/**
 * A factory for creating BiometricProvider objects.
 *
 * @author Arun Bose S A factory for creating BiometricProvider objects.
 */
@Component
public class BiometricProviderFactory {

	/**
	 * Gets the biometric provider.
	 *
	 * @return the biometric provider
	 */

	private static final String IRIS_IMG= "irisImg";

	/** The Constant cogentBiometricProvider. */
	private static final String COGENT_BIO_PROVIDER = "cogent";
	
	@Autowired
	private Environment environment;

	private CogentFingerprintProvider cogentFingerProvider;

	private MantraFingerprintProvider mantraFingerprintProvider;

	private CogentIrisProvider cogentIrisProvider;

	private MorphoIrisProvider morphoIrisProvider;

	@PostConstruct
	public void initProviders() {
		cogentFingerProvider = new CogentFingerprintProvider();
		mantraFingerprintProvider = new MantraFingerprintProvider();
		cogentIrisProvider = new CogentIrisProvider(environment);
		morphoIrisProvider = new MorphoIrisProvider(environment);
	}

	public CogentFingerprintProvider getCogentFingerProvider() {
		return cogentFingerProvider;
	}

	public MantraFingerprintProvider getMantraFingerprintProvider() {
		return mantraFingerprintProvider;
	}

	public CogentIrisProvider getCogentIrisProvider() {
		return cogentIrisProvider;
	}

	public MorphoIrisProvider getMorphoIrisProvider() {
		return morphoIrisProvider;
	}

	/**
	 * Gets the biometric provider.
	 *
	 * @param bioInfo
	 *            the bio info
	 * @return the biometric provider
	 */
	public MosipBiometricProvider getBiometricProvider(BioInfo bioInfo) {

		if (bioInfo.getBioType().equalsIgnoreCase(BiometricProviderFactory.IRIS_IMG)) {
			if (bioInfo.getDeviceInfo().getMake().equalsIgnoreCase(BiometricProviderFactory.COGENT_BIO_PROVIDER))
				return getCogentIrisProvider();
			else
				return getMorphoIrisProvider();
		}

		else {
			if (bioInfo.getDeviceInfo().getMake().equalsIgnoreCase(BiometricProviderFactory.COGENT_BIO_PROVIDER))
				return getCogentFingerProvider();
			else
				return getMantraFingerprintProvider();
		}

	}
}

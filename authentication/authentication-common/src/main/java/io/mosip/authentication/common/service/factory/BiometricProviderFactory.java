package io.mosip.authentication.common.service.factory;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.common.service.impl.match.BioAuthType;
import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.core.spi.provider.bio.CogentFaceProvider;
import io.mosip.authentication.core.spi.provider.bio.CogentFingerprintProvider;
import io.mosip.authentication.core.spi.provider.bio.CogentIrisProvider;
import io.mosip.authentication.core.spi.provider.bio.MantraFingerprintProvider;
import io.mosip.authentication.core.spi.provider.bio.MorphoFaceProvider;
import io.mosip.authentication.core.spi.provider.bio.MorphoIrisProvider;

/**
 * A factory for creating BiometricProvider objects.
 *
 * @author Arun Bose S A factory for creating BiometricProvider objects.
 */
@Component
public class BiometricProviderFactory {

	@Autowired
	private Environment environment;

	private CogentFingerprintProvider cogentFingerProvider;

	private MantraFingerprintProvider mantraFingerprintProvider;

	private CogentIrisProvider cogentIrisProvider;

	private MorphoIrisProvider morphoIrisProvider;

	private CogentFaceProvider cogentFaceProvider;

	private MorphoFaceProvider morphoFaceProvider;

	@PostConstruct
	public void initProviders() {
		cogentFingerProvider = new CogentFingerprintProvider();
		mantraFingerprintProvider = new MantraFingerprintProvider();
		cogentIrisProvider = new CogentIrisProvider(environment);
		morphoIrisProvider = new MorphoIrisProvider(environment);
		cogentFaceProvider = new CogentFaceProvider(environment);
		morphoFaceProvider = new MorphoFaceProvider(environment);

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

	public CogentFaceProvider getCogentFaceProvider() {
		return cogentFaceProvider;
	}

	public MorphoFaceProvider getMorphoFaceProvider() {
		return morphoFaceProvider;
	}

	/**
	 * Gets the biometric provider.
	 *
	 * @param bioInfo the bio info
	 * @return the biometric provider
	 */
	public MosipBiometricProvider getBiometricProvider(DataDTO bioInfo) {

		if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.IRIS_IMG.getType())) {
			return getCogentIrisProvider();
		} else if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FACE_IMG.getType())) {
			return getCogentFaceProvider();
		} else if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_MIN.getType())
				|| bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_IMG.getType())) {
			return getMantraFingerprintProvider();
		}
		return null;
	}

}

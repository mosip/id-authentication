package io.mosip.authentication.service.factory;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.authentication.core.dto.indauth.DataDTO;
import io.mosip.authentication.core.spi.bioauth.provider.MosipBiometricProvider;
import io.mosip.authentication.service.impl.face.CogentFaceProvider;
import io.mosip.authentication.service.impl.face.MorphoFaceProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.CogentFingerprintProvider;
import io.mosip.authentication.service.impl.fingerauth.provider.impl.MantraFingerprintProvider;
import io.mosip.authentication.service.impl.indauth.service.bio.BioAuthType;
import io.mosip.authentication.service.impl.iris.CogentIrisProvider;
import io.mosip.authentication.service.impl.iris.MorphoIrisProvider;

/**
 * A factory for creating BiometricProvider objects.
 *
 * @author Arun Bose S A factory for creating BiometricProvider objects.
 */
@Component
public class BiometricProviderFactory {

	/** The Constant cogentBiometricProvider. */
	private static final String COGENT_FP_PROVIDER = "fingerprint.provider.cogent";

	/** The Constant mantraBiometricProvider. */
	private static final String MANTRA_FP_PROVIDER = "fingerprint.provider.mantra";

	/** The Constant cogentBiometricProvider. */
	private static final String COGENT_IRIS_PROVIDER = "iris.provider.cogent";

	/** The Constant morphoBiometricProvider. */
	private static final String MORPHO_IRIS_PROVIDER = "iris.provider.morpho";

	/** The Constant cogentBiometricProvider. */
	private static final String COGENT_FACE_PROVIDER = "face.provider.cogent";

	/** The Constant morphoBiometricProvider. */
	private static final String MORPHO_FACE_PROVIDER = "face.provider.morpho";

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
			return getIrisProvider(bioInfo);

		} else if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FACE_IMG.getType())) {
			return getFaceProvider(bioInfo);
		} else if (bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_MIN.getType())
				|| bioInfo.getBioType().equalsIgnoreCase(BioAuthType.FGR_IMG.getType())) {
			return getFingerProvider(bioInfo);

		}
		return null;
	}

	/**
	 * Gets the finger provider.
	 *
	 * @param bioInfo the bio info
	 * @return the finger provider
	 */
	private MosipBiometricProvider getFingerProvider(DataDTO bioInfo) {
		// TODO FIXME as dynamically provider has to be changed based on the request
		if (bioInfo.getDeviceProviderID()
				.equalsIgnoreCase(environment.getProperty(BiometricProviderFactory.COGENT_FP_PROVIDER))) {
			return getCogentFingerProvider();
		} else if (bioInfo.getDeviceProviderID()
				.equalsIgnoreCase(environment.getProperty(BiometricProviderFactory.MANTRA_FP_PROVIDER))) {
			return getMantraFingerprintProvider();
		}
		return getMantraFingerprintProvider();
	}

	/**
	 * Gets the face provider.
	 *
	 * @param bioInfo the bio info
	 * @return the face provider
	 */
	private MosipBiometricProvider getFaceProvider(DataDTO bioInfo) {
		if (bioInfo.getDeviceProviderID()
				.equalsIgnoreCase(environment.getProperty(BiometricProviderFactory.COGENT_FACE_PROVIDER))) {
			return getCogentFaceProvider();
		} else if (bioInfo.getDeviceProviderID()
				.equalsIgnoreCase(environment.getProperty(BiometricProviderFactory.MORPHO_FACE_PROVIDER))) {
			return getMorphoFaceProvider();
		}
		return getCogentFaceProvider();
	}

	/**
	 * Gets the iris provider.
	 *
	 * @param bioInfo the bio info
	 * @return the iris provider
	 */
	private MosipBiometricProvider getIrisProvider(DataDTO bioInfo) {
		// TODO FIXME as dynamically provider has to be changed based on the request
		if (bioInfo.getDeviceProviderID()
				.equalsIgnoreCase(environment.getProperty(BiometricProviderFactory.COGENT_IRIS_PROVIDER))) {
			return getCogentIrisProvider();
		} else if (bioInfo.getDeviceProviderID()
				.equalsIgnoreCase(environment.getProperty(BiometricProviderFactory.MORPHO_IRIS_PROVIDER))) {
			return getMorphoIrisProvider();
		}
		return getCogentIrisProvider();
	}

}

package io.mosip.authentication.service.factory;

import org.springframework.beans.factory.annotation.Autowired;
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
 * @author Arun Bose S
 * A factory for creating BiometricProvider objects.
 */
@Component
public class BiometricProviderFactory {
	
    /**
     * Gets the biometric provider.
     *
     * @return the biometric provider
     */
	
	private static final String irisImage ="irisImg";
	
	/** The Constant cogentBiometricProvider. */
	private static final String cogentBiometricProvider ="cogent";
	
	@Autowired
	private CogentFingerprintProvider cogentFingerProvider;
	
	@Autowired
	private MantraFingerprintProvider mantraFingerprintProvider;
	
	@Autowired
	private CogentIrisProvider cogentIrisProvider;
	
	@Autowired
	private MorphoIrisProvider morphoIrisProvider;
	
	
	
    /**
     * Gets the biometric provider.
     *
     * @param bioInfo the bio info
     * @return the biometric provider
     */
    public MosipBiometricProvider getBiometricProvider(BioInfo bioInfo) {
    	
    		if(bioInfo.getBioType().equalsIgnoreCase(BiometricProviderFactory.irisImage)) {
    			if(bioInfo.getDeviceInfo().getMake().equalsIgnoreCase(BiometricProviderFactory.cogentBiometricProvider))
    					return  cogentIrisProvider;
    			else
    				return morphoIrisProvider;
    		}
    		
    		else
    		{
    			if(bioInfo.getDeviceInfo().getMake().equalsIgnoreCase("cogent"))
					return cogentFingerProvider;
			else
				return mantraFingerprintProvider;
    		}
    	
    }
}

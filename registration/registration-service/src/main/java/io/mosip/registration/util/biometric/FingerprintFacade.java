package io.mosip.registration.util.biometric;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.image.WritableImage;

/**
 * It takes a decision based on the input provider name and initialize the respective implementation class and perform the 
 * required operation.
 * 
 * @author M1046564
 *
 */
@Component
public class FingerprintFacade {
	
	private List<MosipFingerprintProvider> fingerprintProviders;
	
	private MosipFingerprintProvider fingerprintProvider;

	/**
	 * provide the minutia of a finger.
	 * @return
	 */
	public String getMinutia() {
		return fingerprintProvider.getMinutia();
	}
	
	public byte[] getIsoTemplate() {
		return fingerprintProvider.getIsoTemplate();
	}

	public String getErrorMessage() {
		return fingerprintProvider.getErrorMessage();
		
	}
	
	/**
	 * 
	 * @return
	 */
	public WritableImage getFingerPrintImage() throws IOException{
		return fingerprintProvider.getFingerPrintImage();
	}
	
	@Autowired
	public void setFingerprintProviders(List<MosipFingerprintProvider> fingerprintProviders) {
		this.fingerprintProviders = fingerprintProviders;
	}
	
	public MosipFingerprintProvider getFingerprintProviderFactory(String make) {
		for(MosipFingerprintProvider mosipFingerprintProvider: fingerprintProviders) {
			if(mosipFingerprintProvider.getClass().getName().toLowerCase().contains(make.toLowerCase())) {
				fingerprintProvider = mosipFingerprintProvider;
			}
		}
		return fingerprintProvider;
	}
	
}

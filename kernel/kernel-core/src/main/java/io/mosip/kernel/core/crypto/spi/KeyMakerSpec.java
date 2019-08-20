package io.mosip.kernel.core.crypto.spi;

/**
 * This interface is specification for KeyMaker component. The component will be
 * responsible for creating keys and storing in HSM.
 * 
 * @author Urvil Joshi
 * 
 * @see KeyGeneratorSpec
 */
public interface KeyMakerSpec {

	/**
	 * 
	 */
	void createAndStoreKeys();
	
}

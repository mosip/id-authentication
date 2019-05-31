package io.mosip.kernel.core.bioapi.spi;

import io.mosip.kernel.core.bioapi.exception.BiometricException;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.cbeffutil.entity.BIR;

/**
 * The Interface IBioApi.
 * 
 * @author Sanjay Murali
 */
public interface IBioApi {

	/**
	 * It checks the quality of the provided biometric image and render the respective quality score.
	 *
	 * @param sample the sample
	 * @param flags the flags
	 * @return the quality score
	 * @throws BiometricException 
	 */
	QualityScore checkQuality(BIR sample, KeyValuePair[] flags) throws BiometricException;
	
	/**
	 * It compares the biometrics and provide the respective matching scores.
	 *
	 * @param sample the sample
	 * @param gallery the gallery
	 * @param flags the flags
	 * @return the score[]
	 */
	Score[] match(BIR sample, BIR[]gallery, KeyValuePair[] flags) throws BiometricException;

	/**
	 * It uses the composite logic while comparing the biometrics and provide the composite matching score. 
	 *
	 * @param sampleList the sample list
	 * @param recordList the record list
	 * @param flags the flags
	 * @return the composite score
	 */
	CompositeScore compositeMatch ( BIR[] sampleList ,BIR[] recordList , KeyValuePair [] flags ) throws BiometricException;

	/**
	 * Extract template.
	 *
	 * @param sample the sample
	 * @param flags the flags
	 * @return the biometric record
	 */
	BIR extractTemplate(BIR sample, KeyValuePair[] flags) throws BiometricException;

	/**
	 * It segment the single biometric image into multiple biometric images.
	 * Eg: Split the thumb slab into multiple fingers
	 *
	 * @param sample the sample
	 * @param flags the flags
	 * @return the biometric record[]
	 */
	BIR[] segment(BIR sample, KeyValuePair[] flags) throws BiometricException;
}

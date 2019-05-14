package io.mosip.kernel.bioapi.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import io.mosip.kernel.core.bioapi.model.BDBInfo;
import io.mosip.kernel.core.bioapi.model.BiometricRecord;
import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.QualityType;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.util.StringUtils;

/**
 * The Class BioApiImpl.
 * 
 * @author Sanjay Murali
 */
public class BioApiImpl implements IBioApi{

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#checkQuality(io.mosip.kernel.core.bioapi.model.BiometricRecord, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public QualityScore checkQuality(BiometricRecord sample, KeyValuePair[] flags) {
		QualityScore qualityScore = new QualityScore();
		int major = Optional.ofNullable(sample.getBdbInfo()).map(BDBInfo::getQuality).map(QualityType::getMajor)
				.orElse(0);
		qualityScore.setInternalScore(major);
		return qualityScore;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#match(io.mosip.kernel.core.bioapi.model.BiometricRecord, io.mosip.kernel.core.bioapi.model.BiometricRecord[], io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public Score[] match(BiometricRecord sample, BiometricRecord[] gallery, KeyValuePair[] flags) {
		Score matchingScore[] = new Score[gallery.length];
		int count =0;
		for (BiometricRecord recordedValue : gallery) {
			matchingScore[count] = new Score();
			if(recordedValue != null && !StringUtils.isEmpty(recordedValue.getBdb()) &&
					recordedValue.getBdb().equalsIgnoreCase(sample.getBdb())) {
				matchingScore[count].setInternalScore(90);
			}else {
				matchingScore[count].setInternalScore(ThreadLocalRandom.current().nextInt(10, 50));
			}
			count ++;
		} 
		return matchingScore;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#compositeMatch(io.mosip.kernel.core.bioapi.model.BiometricRecord[], io.mosip.kernel.core.bioapi.model.BiometricRecord[], io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public CompositeScore compositeMatch(BiometricRecord[] sampleList, BiometricRecord[] recordList,
			KeyValuePair[] flags) {
		Score matchingScore[] = new Score[sampleList.length];
		int count = 0;
		for(BiometricRecord sampleValue : sampleList) {
			Score[] match = match(sampleValue, recordList, flags);
			Optional<Score> max = Arrays.stream(match).max(Comparator.comparing(Score::getInternalScore));
			if(max.isPresent()) {
				matchingScore[count].setInternalScore(max.get().getInternalScore());
				count++;
			}
		}
		double sum = Arrays.stream(matchingScore).mapToDouble(Score::getInternalScore).sum();
		CompositeScore compositeScore = new CompositeScore();
		compositeScore.setIndividualScores(matchingScore);
		compositeScore.setInternalScore((long) (sum/matchingScore.length));
		return compositeScore;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#extractTemplate(io.mosip.kernel.core.bioapi.model.BiometricRecord, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public BiometricRecord extractTemplate(BiometricRecord sample, KeyValuePair[] flags) {
		return sample;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#segment(io.mosip.kernel.core.bioapi.model.BiometricRecord, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public BiometricRecord[] segment(BiometricRecord sample, KeyValuePair[] flags) {
		BiometricRecord[] biometricRecord = new BiometricRecord[1];
		biometricRecord[0]= sample;
		return biometricRecord;
	}

}

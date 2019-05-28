package io.mosip.kernel.bioapi.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;

/**
 * The Class BioApiImpl.
 * 
 * @author Sanjay Murali
 */
@Component
public class BioApiImpl implements IBioApi{

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#checkQuality(io.mosip.kernel.core.bioapi.model.BIR, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public QualityScore checkQuality(BIRType sample, KeyValuePair[] flags) {
		QualityScore qualityScore = new QualityScore();
		int major = Optional.ofNullable(sample.getBDBInfo()).map(BDBInfoType::getQuality).orElse(0);
		qualityScore.setInternalScore(major);
		return qualityScore;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#match(io.mosip.kernel.core.bioapi.model.BIR, io.mosip.kernel.core.bioapi.model.BIR[], io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public Score[] match(BIRType sample, BIRType[] gallery, KeyValuePair[] flags) {
		Score matchingScore[] = new Score[gallery.length];
		int count =0;
		for (BIRType recordedValue : gallery) {
			matchingScore[count] = new Score();
			if(Objects.nonNull(recordedValue) && Objects.nonNull(recordedValue.getBDB()) && recordedValue.getBDB().length != 0 &&
					Arrays.equals(recordedValue.getBDB(), sample.getBDB())) {
				matchingScore[count].setInternalScore(90);
			}else {
				matchingScore[count].setInternalScore(ThreadLocalRandom.current().nextInt(10, 50));
			}
			count ++;
		} 
		return matchingScore;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#compositeMatch(io.mosip.kernel.core.bioapi.model.BIR[], io.mosip.kernel.core.bioapi.model.BIR[], io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public CompositeScore compositeMatch(BIRType[] sampleList, BIRType[] recordList,
			KeyValuePair[] flags) {
		Score matchingScore[] = new Score[sampleList.length];
		int count = 0;
		for(BIRType sampleValue : sampleList) {
			Score[] match = match(sampleValue, recordList, flags);
			Optional<Score> max = Arrays.stream(match).max(Comparator.comparing(Score::getInternalScore));
			if(max.isPresent()) {
				matchingScore[count] = max.get();
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
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#extractTemplate(io.mosip.kernel.core.bioapi.model.BIR, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public BIRType extractTemplate(BIRType sample, KeyValuePair[] flags) {
		return sample;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#segment(io.mosip.kernel.core.bioapi.model.BIR, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public BIRType[] segment(BIRType sample, KeyValuePair[] flags) {
		BIRType[] bir = new BIRType[1];
		bir[0]= sample;
		return bir;
	}

}

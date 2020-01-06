package io.mosip.kernel.bioapi.impl;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.bioapi.model.CompositeScore;
import io.mosip.kernel.core.bioapi.model.KeyValuePair;
import io.mosip.kernel.core.bioapi.model.QualityScore;
import io.mosip.kernel.core.bioapi.model.Score;
import io.mosip.kernel.core.bioapi.spi.IBioApi;
import io.mosip.kernel.core.cbeffutil.entity.BDBInfo;
import io.mosip.kernel.core.cbeffutil.entity.BIR;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.QualityType;

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
	public QualityScore checkQuality(BIR sample, KeyValuePair[] flags) {
		QualityScore qualityScore = new QualityScore();
		int major = Optional.ofNullable(sample.getBdbInfo()).map(BDBInfo::getQuality)
				.map(QualityType::getScore).orElse(0L).intValue();
		qualityScore.setInternalScore(major);
		return qualityScore;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#match(io.mosip.kernel.core.bioapi.model.BIR, io.mosip.kernel.core.bioapi.model.BIR[], io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public Score[] match(BIR sample, BIR[] gallery, KeyValuePair[] flags) {
		Score matchingScore[] = new Score[gallery.length];
		int count =0;
		for (BIR recordedValue : gallery) {
			matchingScore[count] = new Score();
			if(Objects.nonNull(recordedValue) && Objects.nonNull(recordedValue.getBdb()) && recordedValue.getBdb().length != 0 &&
					Arrays.equals(recordedValue.getBdb(), sample.getBdb())) {
				matchingScore[count].setInternalScore(90);
			}else {
				matchingScore[count].setInternalScore(new SecureRandom().nextInt(50));
			}
			count ++;
		} 
		return matchingScore;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#compositeMatch(io.mosip.kernel.core.bioapi.model.BIR[], io.mosip.kernel.core.bioapi.model.BIR[], io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public CompositeScore compositeMatch(BIR[] sampleList, BIR[] recordList,
			KeyValuePair[] flags) {
		Score matchingScore[] = new Score[sampleList.length];
		int count = 0;
		for(BIR sampleValue : sampleList) {
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
	public BIR extractTemplate(BIR sample, KeyValuePair[] flags) {
		return sample;
	}

	/* (non-Javadoc)
	 * @see io.mosip.kernel.core.bioapi.spi.IBioApi#segment(io.mosip.kernel.core.bioapi.model.BIR, io.mosip.kernel.core.bioapi.model.KeyValuePair[])
	 */
	@Override
	public BIR[] segment(BIR sample, KeyValuePair[] flags) {
		BIR[] bir = new BIR[1];
		bir[0]= sample;
		return bir;
	}

}

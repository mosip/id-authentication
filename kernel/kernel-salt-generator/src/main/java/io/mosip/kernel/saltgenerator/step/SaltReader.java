package io.mosip.kernel.saltgenerator.step;

import static io.mosip.kernel.saltgenerator.constant.SaltGeneratorConstant.END_SEQ;
import static io.mosip.kernel.saltgenerator.constant.SaltGeneratorConstant.START_SEQ;

import javax.annotation.PostConstruct;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;
import io.mosip.kernel.saltgenerator.entity.SaltEntity;
import io.mosip.kernel.saltgenerator.logger.SaltGeneratorLogger;

/**
 * The Class SaltReader - Creates entities based on chunk size.
 * Start and end sequence for entity Id is provide via configuration.
 * Implements {@code ItemReader}.
 * Salt is provided by {@code HMACUtils.generateSalt()}
 *
 * @author Manoj SP
 */
@Component
public class SaltReader implements ItemReader<SaltEntity> {
	
	/** The mosip logger. */
	Logger mosipLogger = SaltGeneratorLogger.getLogger(SaltReader.class);

	/** The start seq. */
	private Long startSeq;

	/** The end seq. */
	private Long endSeq;

	/** The env. */
	@Autowired
	private Environment env;

	@PostConstruct
	public void initialize() {
		startSeq = env.getProperty(START_SEQ.getValue(), Long.class);
		endSeq = env.getProperty(END_SEQ.getValue(), Long.class);
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemReader#read()
	 */
	@Override
	public SaltEntity read() {
		if (startSeq <= endSeq) {
			SaltEntity entity = new SaltEntity();
			entity.setId(startSeq);
			entity.setSalt(CryptoUtil.encodeBase64String(HMACUtils.generateSalt()));
			entity.setCreatedBy("System");
			entity.setCreateDtimes(DateUtils.getUTCCurrentDateTime());
			mosipLogger.debug("SALT_GENERATOR", "SaltReader", "Entity with id created - ",
					String.valueOf(startSeq));
			startSeq = startSeq + 1;
			return entity;
		} else {
			return null;
		}
	}

}

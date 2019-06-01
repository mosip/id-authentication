package io.mosip.idrepository.saltgenerator.step;

import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.END_SEQ;
import static io.mosip.idrepository.saltgenerator.constant.IdRepoSaltGeneratorConstant.START_SEQ;

import javax.annotation.PostConstruct;

import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.saltgenerator.entity.SaltEntity;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.HMACUtils;

/**
 * @author Manoj SP
 *
 */
@Component
public class SaltReader implements ItemReader<SaltEntity> {
	
	Logger mosipLogger = IdRepoLogger.getLogger(SaltReader.class);

	private Long startSeq;

	private Long endSeq;

	@Autowired
	private Environment env;

	@PostConstruct
	public void initialize() {
		startSeq = env.getProperty(START_SEQ.getValue(), Long.class);
		endSeq = env.getProperty(END_SEQ.getValue(), Long.class);
	}

	@Override
	public SaltEntity read() {
		if (startSeq <= endSeq) {
			SaltEntity entity = new SaltEntity();
			entity.setId(startSeq++);
			entity.setSalt(HMACUtils.digestAsPlainText(HMACUtils.generateSalt()));
			entity.setCreatedBy("updated");
			entity.setCreateDtimes(DateUtils.getUTCCurrentDateTime());
			entity.setUpdatedBy("updated");
			entity.setUpdatedDtimes(DateUtils.getUTCCurrentDateTime());
			mosipLogger.debug("ID_REPO_SALT_GENERATOR", "SaltReader", "Entity with id created - ",
					String.valueOf(startSeq));
			return entity;
		} else {
			return null;
		}
	}

}

package io.mosip.kernel.saltgenerator.step;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.saltgenerator.constant.SaltGeneratorErrorConstants;
import io.mosip.kernel.core.saltgenerator.exception.SaltGeneratorException;
import io.mosip.kernel.saltgenerator.entity.SaltEntity;
import io.mosip.kernel.saltgenerator.logger.SaltGeneratorLogger;
import io.mosip.kernel.saltgenerator.repository.SaltRepository;

/**
 * The Class SaltWriter - Class to write salt entities to DB in batch.
 * Implements {@code ItemWriter}.
 *
 * @author Manoj SP
 */
@Component
public class SaltWriter implements ItemWriter<SaltEntity> {

	Logger mosipLogger = SaltGeneratorLogger.getLogger(SaltWriter.class);

	@Autowired
	private SaltRepository repo;

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	@Transactional
	public void write(List<? extends SaltEntity> entities) throws Exception {
		if (repo.countByIdIn(entities.parallelStream().map(SaltEntity::getId).collect(Collectors.toList())) == 0l) {
			repo.saveAll(entities);
			mosipLogger.debug("SALT_GENERATOR", "SaltWriter", "Entities written", String.valueOf(entities.size()));
		} else {
			mosipLogger.error("SALT_GENERATOR", "SaltWriter", "write", "Records already exists");
			throw new SaltGeneratorException(SaltGeneratorErrorConstants.RECORD_EXISTS);
		}
	}

}

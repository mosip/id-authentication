package io.mosip.idrepository.saltgenerator.step;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.saltgenerator.entity.SaltEntity;
import io.mosip.idrepository.saltgenerator.repository.SaltRepository;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * @author Manoj SP
 *
 */
@Component
public class SaltWriter implements ItemWriter<SaltEntity> {

	Logger mosipLogger = IdRepoLogger.getLogger(SaltWriter.class);

	@Autowired
	private SaltRepository repo;

	@Override
	@Transactional
	public void write(List<? extends SaltEntity> entities) throws Exception {
		if (repo.countAllById(entities.parallelStream().map(SaltEntity::getId).collect(Collectors.toList())) == 0l) {
			repo.saveAll(entities);
			mosipLogger.debug("ID_REPO_SALT_GENERATOR", "SaltWriter", "Entities written", String.valueOf(entities.size()));
		} else {
			throw new IdRepoAppException(IdRepoErrorConstants.RECORD_EXISTS);
		}
	}

}

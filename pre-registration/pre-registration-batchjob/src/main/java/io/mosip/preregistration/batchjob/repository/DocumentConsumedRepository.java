package io.mosip.preregistration.batchjob.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.batchjob.entity.DocumentEntityConsumed;
@Repository("documentConsumedRepository")
public interface DocumentConsumedRepository extends BaseRepository<DocumentEntityConsumed, String> {

}

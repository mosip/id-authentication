package io.mosip.pregistration.datasync.repository;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.pregistration.datasync.entity.ReverseDataSyncEntity;

/**
 * @author M1046129
 *
 */
@Repository("dataSyncRepository")
@Transactional
public interface DataSyncRepository extends BaseRepository<ReverseDataSyncEntity, String> {
	
}

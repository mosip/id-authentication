package io.mosip.preregistration.datasync.repository;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.preregistration.datasync.entity.InterfaceDataSyncEntity;

/**
 * @author M1046129
 *
 */
@Repository("dataSyncRepository")
@Transactional
public interface InterfaceDataSyncRepo extends BaseRepository<InterfaceDataSyncEntity, String> {
	
}

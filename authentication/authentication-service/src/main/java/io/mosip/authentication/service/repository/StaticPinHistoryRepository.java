package io.mosip.authentication.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.StaticPinHistoryEntity;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
/**
 *  This is a repository class for entity {@link StaticPinHistoryEntity}.
 *  
 * @author Prem Kumar
 *
 */
@Repository
public interface StaticPinHistoryRepository extends BaseRepository<StaticPinHistoryEntity, String> {

}

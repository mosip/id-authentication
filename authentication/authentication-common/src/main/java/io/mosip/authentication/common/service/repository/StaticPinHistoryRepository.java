package io.mosip.authentication.common.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.StaticPinHistory;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface StaticPinHistoryRepository.
 * 
 * @author Prem Kumar
 */
@Repository
public interface StaticPinHistoryRepository extends BaseRepository<StaticPinHistory, String> {

}

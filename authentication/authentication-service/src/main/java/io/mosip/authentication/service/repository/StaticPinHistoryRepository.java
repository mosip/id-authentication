package io.mosip.authentication.service.repository;

import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.StaticPinHistory;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
/**
 *  This is a repository class for entity {@link StaticPinHistory}.
 *  
 * @author Prem Kumar
 *
 */
@Repository
public interface StaticPinHistoryRepository extends BaseRepository<StaticPinHistory, String> {

}

package io.mosip.registration.processor.packet.storage.repository;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.spi.dataaccess.repository.BaseRepository;
import io.mosip.registration.processor.packet.storage.entity.ApplicantDemographicEntity;
/**
 * 
 * @author Horteppa M1048399
 *
 */
@Repository
public interface ApplicantDemographicRepository extends BaseRepository<ApplicantDemographicEntity, String> {

}

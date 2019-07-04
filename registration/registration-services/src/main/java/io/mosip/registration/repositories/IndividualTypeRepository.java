package io.mosip.registration.repositories;

import java.util.List;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.IndividualType;
import io.mosip.registration.entity.id.IndividualTypeId;

/**
 * Repository class to handle CRUD operations for
 * {@link IndividualTypeRepository}}
 * 
 * @author Sreekar Chukka
 *
 */
public interface IndividualTypeRepository extends BaseRepository<IndividualType, IndividualTypeId> {
	List<IndividualType> findByIndividualTypeIdCodeAndIndividualTypeIdLangCodeAndIsActiveTrue(String code,
			String langCode);
}

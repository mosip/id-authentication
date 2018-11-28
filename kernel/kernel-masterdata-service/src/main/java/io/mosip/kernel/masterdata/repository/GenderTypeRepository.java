package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.GenderType;

/**
 * Repository class for fetching gender data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface GenderTypeRepository extends BaseRepository<GenderType, String> {

	List<GenderType> findGenderByLanguageCodeAndIsDeletedFalseOrIsDeletedIsNull(String languageCode);

}

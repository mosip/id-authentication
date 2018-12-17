package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Gender;

/**
 * Repository class for fetching gender data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@Repository
public interface GenderTypeRepository extends BaseRepository<Gender, String> {

	@Query("FROM Gender WHERE langCode =?1 and (isDeleted is null or isDeleted =false)")
	List<Gender> findGenderByLangCodeAndIsDeletedFalseOrIsDeletedIsNull(String langCode);

}

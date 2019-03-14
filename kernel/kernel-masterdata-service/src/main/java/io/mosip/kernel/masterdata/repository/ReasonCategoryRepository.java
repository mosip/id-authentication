package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.ReasonCategory;

/**
 * 
 * @author Srinivasan
 *
 */
@Repository
public interface ReasonCategoryRepository extends BaseRepository<ReasonCategory, String> {

	@Query("FROM ReasonCategory where (isDeleted is null OR isDeleted = false) AND isActive = true")
	List<ReasonCategory> findReasonCategoryByIsDeletedFalseOrIsDeletedIsNull();

	@Query("FROM ReasonCategory r where r.code=?1 and r.langCode=?2 and (r.isDeleted is null or r.isDeleted=false) and r.isActive = true")
	List<ReasonCategory> findReasonCategoryByCodeAndLangCode(String code, String languageCode);

}

package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.ReasonCategory;

public interface ReasonCategoryRepository extends BaseRepository<ReasonCategory, String> {

	/**
	 * 
	 * @return
	 */
	List<ReasonCategory> findReasonCategoryByIsDeletedFalse();

	/**
	 * 
	 * @param code
	 * @param languageCode
	 * @return
	 */
	List<ReasonCategory> findReasonCategoryByCodeAndLangCodeAndIsDeletedFalse(String code, String languageCode);

	@Query(value = "select rc.code,rc.lang_code,rc.cr_by,rc.cr_dtimes,rc.del_dtimes,rc.is_active,rc.is_deleted,rc.upd_by,rc.upd_dtimes,rc.descr,rc.name from master.reason_category rc where rc.cr_dtimes > ?1 or rc.upd_dtimes > ?1 or rc.del_dtimes > ?1", nativeQuery = true)
	List<ReasonCategory> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);

	@Query(value = "select rc.code,rc.lang_code,rc.cr_by,rc.cr_dtimes,rc.del_dtimes,rc.is_active,rc.is_deleted,rc.upd_by,rc.upd_dtimes,rc.descr,rc.name from master.reason_category rc", nativeQuery = true)
	List<ReasonCategory> findAllReasons();
}

package io.mosip.kernel.synchandler.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.synchandler.entity.DocumentType;

/**
 * 
 * @author Abhishek Kumar
 * @author Uday Kumar
 * @since 1.0.0
 *
 */
@Repository
public interface DocumentTypeRepository extends BaseRepository<DocumentType, String> {
	@Query(value = "select dt.code, dt.name, dt.descr , dt.lang_code , dt.is_active ,dt.cr_by ,dt.cr_dtimes ,dt.upd_by ,dt.upd_dtimes ,dt.is_deleted ,dt.del_dtimes from master.valid_document vd , master.doc_type dt , master.doc_category dc where vd.doctyp_code = dt.code and dt.is_deleted = false and vd.doccat_code = dc.code and dc.code = ?1 and dc.lang_code = ?2", nativeQuery = true)
	List<DocumentType> findByCodeAndLangCodeAndIsDeletedFalse(String code, String langCode);

	@Query("FROM DocumentType WHERE createdDateTime > ?1 OR updatedDateTime > ?1  OR deletedDateTime > ?1")
	List<DocumentType> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
}

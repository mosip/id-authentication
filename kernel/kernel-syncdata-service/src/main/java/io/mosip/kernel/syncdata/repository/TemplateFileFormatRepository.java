package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.TemplateFileFormat;

@Repository
public interface TemplateFileFormatRepository extends BaseRepository<TemplateFileFormat, String> {
	@Query(value="SELECT ff.code, ff.descr, ff.lang_code, ff.is_active, ff.cr_by, ff.cr_dtimes, ff.upd_by, ff.upd_dtimes, ff.is_deleted, ff.del_dtimes FROM master.template_file_format ff where ff.cr_dtimes >?1 or upd_dtimes >?1 or del_dtimes > ?1",nativeQuery=true)
	List<TemplateFileFormat> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);
	
	@Query(value="SELECT code, descr, lang_code, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes FROM master.template_file_format",nativeQuery=true)
	List<TemplateFileFormat> findAllTemplateFormat();
}

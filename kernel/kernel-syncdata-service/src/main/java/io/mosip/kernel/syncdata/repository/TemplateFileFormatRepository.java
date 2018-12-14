package io.mosip.kernel.syncdata.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.syncdata.entity.TemplateFileFormat;

@Repository
public interface TemplateFileFormatRepository extends BaseRepository<TemplateFileFormat, String> {
	/**
	 * Method to find list of TemplateFileFormat created , updated or deleted time is
	 * greater than lastUpdated timeStamp.
	 * 
	 * @param lastUpdated
	 *            timeStamp
	 * @return list of {@link TemplateFileFormat}
	 */
	@Query(value = "SELECT ff.code, ff.descr, ff.lang_code, ff.is_active, ff.cr_by, ff.cr_dtimes, ff.upd_by, ff.upd_dtimes, ff.is_deleted, ff.del_dtimes FROM master.template_file_format ff where ff.cr_dtimes >?1 or upd_dtimes >?1 or del_dtimes > ?1", nativeQuery = true)
	List<TemplateFileFormat> findAllLatestCreatedUpdateDeleted(LocalDateTime lastUpdated);

	/**
	 * Method to fetch all the TemplateFileFormats
	 * 
	 * @return list of {@link TemplateFileFormat}
	 */
	@Query(value = "SELECT code, descr, lang_code, is_active, cr_by, cr_dtimes, upd_by, upd_dtimes, is_deleted, del_dtimes FROM master.template_file_format", nativeQuery = true)
	List<TemplateFileFormat> findAllTemplateFormat();
}

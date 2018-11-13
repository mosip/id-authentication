package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.Title;


@Repository
public interface TitleRepository extends BaseRepository<Title, String> {
	@Query
	List<Title> getThroughLanguageCode(@Param("lang_code")String languageCode);

}

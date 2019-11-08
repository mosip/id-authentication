package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.entity.DaysOfWeek;
import io.mosip.kernel.masterdata.entity.id.WeekDayId;

@Repository("daysOfWeekRepo")
public interface DaysOfWeekListRepo extends BaseRepository<DaysOfWeek, WeekDayId>{
	
	@Query("SELECT d.name from DaysOfWeek d where d.code=?1 and d.langCode=?2 and (d.isDeleted is null or d.isDeleted = false) and d.isActive = true")
	List<String> findBycodeAndlangCode(String dayCode, String langCode);

}

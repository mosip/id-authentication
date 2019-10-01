package io.mosip.kernel.masterdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.kernel.masterdata.dto.DayNameAndSeqListDto;
import io.mosip.kernel.masterdata.dto.getresponse.WorkingDaysDto;
import io.mosip.kernel.masterdata.entity.RegWorkingNonWorking;
import io.mosip.kernel.masterdata.entity.id.RegWorkingNonWorkingId;

@Repository("workingDaysRepo")
public interface RegWorkingNonWorkingRepo extends BaseRepository<RegWorkingNonWorking, RegWorkingNonWorkingId>{
	
	@Query("SELECT new io.mosip.kernel.masterdata.dto.DayNameAndSeqListDto(d.name,d.daySeq) "+"FROM RegWorkingNonWorking w RIGHT JOIN w.daysOfWeek d "+"where w.registrationCenterId=?1 and w.languagecode=?2 and w.isWorking=true and (w.isDeleted is null or w.isDeleted = false) and w.isActive = true")
	List<DayNameAndSeqListDto> findByregistrationCenterIdAndlanguagecode(String regCenterId,String langcode);
	
	@Query("SELECT new io.mosip.kernel.masterdata.dto.getresponse.WorkingDaysDto(d.name,w.dayCode,w.languagecode,w.isActive) "+"FROM RegWorkingNonWorking w RIGHT JOIN w.daysOfWeek d "+"where w.registrationCenterId=?1 and w.dayCode=?2 and w.isWorking=true and w.isActive = true")
	List<WorkingDaysDto> findByregistrationCenterIdAnddayCode(String regCenterId,String daycode);

}

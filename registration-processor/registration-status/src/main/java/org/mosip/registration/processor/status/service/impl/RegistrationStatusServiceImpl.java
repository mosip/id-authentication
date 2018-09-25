package org.mosip.registration.processor.status.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.mosip.registration.processor.status.dao.EntityStatusBaseDao;
import org.mosip.registration.processor.status.dao.RegistrationStatusDao;
import org.mosip.registration.processor.status.dto.RegistrationStatusDto;
import org.mosip.registration.processor.status.entity.RegistrationStatusEntity;
import org.mosip.registration.processor.status.exception.TablenotAccessibleException;
import org.mosip.registration.processor.status.service.RegistrationStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RegistrationStatusServiceImpl implements RegistrationStatusService<String, RegistrationStatusDto> {

	@Value("${landingZone_To_VirusScan_Interval_Threshhold_time}")
	private int threshholdTime;
	@Autowired
	private RegistrationStatusDao registrationStatusDao;
	@Autowired
	@Qualifier("entityStatusBaseDao")
	private EntityStatusBaseDao entityStatusBaseDao;

	@Override
	public RegistrationStatusDto getRegistrationStatus(String enrolmentId) {
		try {
			Optional<RegistrationStatusEntity> entity = registrationStatusDao.findById(enrolmentId);
			return entity.isPresent() ? convertEntityToDto(entity.get()) : null;
		} catch (Exception e) {
			throw new TablenotAccessibleException("Could not get Information from table", e);
		}

	}

	@Override
	public List<RegistrationStatusDto> findbyfilesByThreshold(String statusCode) {
		try {
			List<RegistrationStatusEntity> entities = entityStatusBaseDao.findbyfilesByThreshold(statusCode,
					getThreshholdTime());
			return convertEntityListToDtoList(entities);
		} catch (Exception e) {
			throw new TablenotAccessibleException("Could not get Information from table", e);
		}

	}

	@Override
	public void addRegistrationStatus(RegistrationStatusDto registrationStatusDto) {
		try {
			RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
			registrationStatusDao.save(entity);
		} catch (Exception e) {
			throw new TablenotAccessibleException("Could not add Information to table", e);
		}
	}

	@Override
	public void updateRegistrationStatus(RegistrationStatusDto registrationStatusDto) {
		try {
			RegistrationStatusDto dto = getRegistrationStatus(registrationStatusDto.getEnrolmentId());
			if (dto != null) {
				RegistrationStatusEntity entity = convertDtoToEntity(registrationStatusDto);
				entity.setCreateDateTime(dto.getCreateDateTime());
				registrationStatusDao.save(entity);
			}
		} catch (Exception e) {
			throw new TablenotAccessibleException("Could not update Information to table", e);
		}
	}

	@Override
	public List<RegistrationStatusDto> getByStatus(String status) {
		List<RegistrationStatusEntity> registrations = registrationStatusDao.getEnrolmentStatusByStatus(status);
		List<RegistrationStatusDto> registrationIds = new ArrayList<>();
		for (RegistrationStatusEntity entity : registrations) {
			registrationIds.add(convertEntityToDto(entity));
		}
		return registrationIds;

	}

	@Override
	public List<RegistrationStatusDto> getByIds(String ids) {
		String registrationIdArray[] = ids.split(",");
		List<String> registrationIds = Arrays.asList(registrationIdArray);
		List<RegistrationStatusEntity> dao = entityStatusBaseDao.getByIds(registrationIds);
		return convertEntityListToDtoList(dao);
	}

	private List<RegistrationStatusDto> convertEntityListToDtoList(List<RegistrationStatusEntity> entities) {
		List<RegistrationStatusDto> list = new ArrayList<>();
		if (entities != null) {
			for (RegistrationStatusEntity entity : entities) {
				list.add(convertEntityToDto(entity));
			}
			return list;
		} else
			return list;
	}

	private RegistrationStatusDto convertEntityToDto(RegistrationStatusEntity entity) {
		return new RegistrationStatusDto(entity.getEnrolmentId(), entity.getStatus(), entity.getRetryCount(),
				entity.getCreateDateTime(), entity.getUpdateDateTime());
	}

	private RegistrationStatusEntity convertDtoToEntity(RegistrationStatusDto dto) {
		return new RegistrationStatusEntity(dto.getEnrolmentId(), dto.getStatus(), dto.getRetryCount());
	}

	public int getThreshholdTime() {
		return this.threshholdTime;
	}

}

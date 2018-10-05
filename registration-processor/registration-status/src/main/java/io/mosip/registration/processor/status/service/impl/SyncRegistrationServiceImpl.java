/**
 * 
 */
package io.mosip.registration.processor.status.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.SyncRegistrationService;

/**
 * The Class SyncRegistrationServiceImpl.
 *
 * @author M1047487
 */
@Component
public class SyncRegistrationServiceImpl implements SyncRegistrationService<SyncRegistrationDto> {

	@Autowired
	private SyncRegistrationDao syncRegistrationDao;


	private static final String COULD_NOT_GET = "Could not get Information from table";
	
	/**
	 * Instantiates a new sync registration service impl.
	 */
	public SyncRegistrationServiceImpl() {
		super();
	}

	@Override
	public List<SyncRegistrationDto> sync(List<SyncRegistrationDto> syncResgistrationdto) {
		List<SyncRegistrationDto> list = new ArrayList<>();
			
			for(SyncRegistrationDto syncRegistrationDto: syncResgistrationdto) {
				if(!isPresent(syncRegistrationDto.getRegistrationId())) {
					list.add(convertEntityToDto(syncRegistrationDao.save(convertDtoToEntity(syncRegistrationDto))));
				}
			}
			
		return list;
	}

	@Override
	public boolean isPresent(String syncResgistrationId) {
		return syncRegistrationDao.findById(syncResgistrationId)!=null;
	}
	

	
	/**
	 * Convert entity to dto.
	 *
	 * @param entities
	 *            the entities
	 * @return the list
	 */
	@SuppressWarnings("unused")
	private List<SyncRegistrationDto> convertEntityToDto(List<SyncRegistrationEntity> entities) {
		List<SyncRegistrationDto> ents = new ArrayList<>();
		for(SyncRegistrationEntity entity: entities) {
			ents.add(convertEntityToDto(entity));
		}
		return ents;
	}

	/**
	 * Convert dto to entity.
	 *
	 * @param entities
	 *            the entities
	 * @return the list
	 */
	private List<SyncRegistrationEntity> convertDtoToEntity(List<SyncRegistrationDto> entities) {
		List<SyncRegistrationEntity> ents = new ArrayList<>();
		for(SyncRegistrationDto entity: entities) {
			ents.add(convertDtoToEntity(entity));
		}
		return ents;
	}

	private SyncRegistrationDto convertEntityToDto(SyncRegistrationEntity entity) {
		SyncRegistrationDto syncRegistrationDto = new SyncRegistrationDto();
		syncRegistrationDto.setRegistrationId(entity.getRegistrationId());
		syncRegistrationDto.setIsActive(entity.getIsActive());
		syncRegistrationDto.setIsDeleted(entity.getIsDeleted());
		syncRegistrationDto.setLangCode(entity.getLangCode());
		syncRegistrationDto.setParentRegistrationId(entity.getParentRegistrationId());
		syncRegistrationDto.setStatusCode(entity.getStatusCode());
		syncRegistrationDto.setSyncRegistrationId(entity.getSyncRegistrationId());
		syncRegistrationDto.setStatusComment(entity.getStatusComment());
		
		syncRegistrationDto.setCreatedBy(entity.getCreatedBy());
		syncRegistrationDto.setCreateDateTime(entity.getCreateDateTime());
		syncRegistrationDto.setUpdatedBy(entity.getUpdatedBy());
		syncRegistrationDto.setUpdateDateTime(entity.getUpdateDateTime());
		syncRegistrationDto.setDeletedDateTime(entity.getDeletedDateTime());
		return syncRegistrationDto;
	}

	private SyncRegistrationEntity convertDtoToEntity(SyncRegistrationDto dto) {
		SyncRegistrationEntity syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId(dto.getRegistrationId());
		syncRegistrationEntity.setIsActive(dto.getIsActive());
		syncRegistrationEntity.setIsDeleted(dto.getIsDeleted());
		syncRegistrationEntity.setLangCode(dto.getLangCode());
		syncRegistrationEntity.setParentRegistrationId(dto.getParentRegistrationId());
		syncRegistrationEntity.setStatusCode(dto.getStatusCode());
		syncRegistrationEntity.setSyncRegistrationId(dto.getSyncRegistrationId());
		syncRegistrationEntity.setStatusComment(dto.getStatusComment());
		
		syncRegistrationEntity.setCreatedBy(dto.getCreatedBy());
		syncRegistrationEntity.setCreateDateTime(dto.getCreateDateTime());
		syncRegistrationEntity.setUpdatedBy(dto.getUpdatedBy());
		syncRegistrationEntity.setUpdateDateTime(dto.getUpdateDateTime());
		syncRegistrationEntity.setDeletedDateTime(dto.getDeletedDateTime());
		return syncRegistrationEntity;
	}
}

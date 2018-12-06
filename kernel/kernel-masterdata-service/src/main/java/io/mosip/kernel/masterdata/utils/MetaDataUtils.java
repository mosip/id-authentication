package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EmbeddedId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.entity.BaseEntity;
import io.mosip.kernel.masterdata.entity.Machine;
import io.mosip.kernel.masterdata.entity.MachineHistory;

@Component
@SuppressWarnings("unchecked")
public class MetaDataUtils {

	@Autowired
	private DataMapper dataMapper;

	@Autowired
	MapperUtils mapperUtils;

	
	public <T, D extends BaseEntity> List<D> setCreateMetaData(final Collection<T> dtoList,
			Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
		List<D> entities = new ArrayList<>();

		dtoList.forEach(dto -> {
			D entity = (D) dataMapper.map(dto, entityClass, true, null, null, true);
			setCreatedDateTime(contextUser, entity);
			entities.add(entity);
		});

		return entities;

	}

	private <D extends BaseEntity> void setCreatedDateTime(String contextUser, D entity) {
		entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setCreatedBy(contextUser);
	}

	public MachineHistory createdMachineHistory(Machine machine) {

		LocalDateTime etime = LocalDateTime.now(ZoneId.of("UTC"));
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();

		MachineHistory machineHistory = new MachineHistory();
		machineHistory.setId(machine.getId());
		machineHistory.setName(machine.getName());
		machineHistory.setMacAddress(machine.getMacAddress());
		machineHistory.setSerialNum(machine.getSerialNum());
		machineHistory.setIpAddress(machine.getIpAddress());
		machineHistory.setMachineSpecId(machine.getMachineSpecId());
		machineHistory.setLangCode(machine.getLangCode());
		machineHistory.setIsActive(machine.getIsActive());
		machineHistory.setValidityDateTime(machine.getValidityDateTime());

		setCreatedDateTime(contextUser, machineHistory);
		machineHistory.setEffectDateTime(etime);

		return machineHistory;

	}

	// -----------------------------------------
	public Machine createdMachine(MachineDto machineDto) {

		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();

		Machine machine = new Machine();
		machine.setId(machineDto.getId());
		machine.setName(machineDto.getName());
		machine.setMacAddress(machineDto.getMacAddress());
		machine.setSerialNum(machineDto.getSerialNum());
		machine.setIpAddress(machineDto.getIpAddress());
		machine.setMachineSpecId(machineDto.getMachineSpecId());
		machine.setLangCode(machineDto.getLangCode());
		machine.setIsActive(machineDto.getIsActive());
		machine.setValidityDateTime(machineDto.getValidityDateTime());

		setCreatedDateTime(contextUser, machine);
		return machine;

	}

	public <T, D extends BaseEntity> D setCreateMetaData(final T dto, Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();

		D entity = (D) mapperUtils.mapNew(dto, entityClass);

		Field[] fields = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(EmbeddedId.class)) {
				try {
					Object id = field.getType().newInstance();
					mapperUtils.mapNew(dto, id);
					field.setAccessible(true);
					field.set(entity, id);
					field.setAccessible(false);
					break;
				} catch (Exception e) {
					throw new DataAccessLayerException("KER-MSD-000", "Error while mapping Embedded Id fields", e);
				}
			}
		}

		setCreatedDateTime(contextUser, entity);
		return entity;
	}

}

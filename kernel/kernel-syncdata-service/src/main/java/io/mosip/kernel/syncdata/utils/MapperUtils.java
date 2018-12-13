package io.mosip.kernel.syncdata.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.syncdata.converter.RegistrationCenterConverter;
import io.mosip.kernel.syncdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.syncdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.syncdata.dto.DeviceTypeDto;
import io.mosip.kernel.syncdata.dto.HolidayDto;
import io.mosip.kernel.syncdata.dto.LocationHierarchyDto;
import io.mosip.kernel.syncdata.dto.MachineDto;
import io.mosip.kernel.syncdata.dto.MachineSpecificationDto;
import io.mosip.kernel.syncdata.dto.MachineTypeDto;
import io.mosip.kernel.syncdata.dto.ReasonCategoryDto;
import io.mosip.kernel.syncdata.dto.ReasonListDto;
import io.mosip.kernel.syncdata.dto.RegistrationCenterDto;
import io.mosip.kernel.syncdata.dto.TitleDto;
import io.mosip.kernel.syncdata.entity.DeviceSpecification;
import io.mosip.kernel.syncdata.entity.DeviceType;
import io.mosip.kernel.syncdata.entity.Holiday;
import io.mosip.kernel.syncdata.entity.Machine;
import io.mosip.kernel.syncdata.entity.MachineSpecification;
import io.mosip.kernel.syncdata.entity.MachineType;
import io.mosip.kernel.syncdata.entity.ReasonCategory;
import io.mosip.kernel.syncdata.entity.RegistrationCenter;
import io.mosip.kernel.syncdata.entity.Title;
import io.mosip.kernel.syncdata.entity.id.HolidayID;

@Component
public class MapperUtils {

	@Autowired
	private DataMapper dataMapperImpl;

	public <E, D> D map(final E entity, D object) {
		dataMapperImpl.map(entity, object, true, null, null, true);
		return object;
	}

	public <D, T> D map(final T entity, Class<D> outCLass) {
		return dataMapperImpl.map(entity, outCLass, true, null, null, true);

	}

	public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
		return entityList.stream().map(entity -> map(entity, outCLass)).collect(Collectors.toList());
	}

	public List<RegistrationCenterDto> mapRegistrationCenter(List<RegistrationCenter> list) {
		List<RegistrationCenterDto> responseDto = new ArrayList<>();
		list.forEach(p -> {
			RegistrationCenterDto dto = new RegistrationCenterDto();
			dataMapperImpl.map(p, dto, new RegistrationCenterConverter());
			dataMapperImpl.map(p, dto, true, null, null, true);
			responseDto.add(dto);
		});

		return responseDto;
	}

	public List<HolidayDto> mapHolidays(List<Holiday> holidays) {
		Objects.requireNonNull(holidays);
		List<HolidayDto> holidayDtos = new ArrayList<>();
		holidays.forEach(holiday -> {
			LocalDate date = holiday.getHolidayId().getHolidayDate();
			HolidayID holidayId = holiday.getHolidayId();
			HolidayDto dto = new HolidayDto();
			dto.setHolidayId(String.valueOf(holidayId.getId()));
			dto.setHolidayDate(String.valueOf(date));
			dto.setHolidayName(holiday.getHolidayName());
			dto.setLanguageCode(holidayId.getLangCode());
			dto.setHolidayYear(String.valueOf(date.getYear()));
			dto.setHolidayMonth(String.valueOf(date.getMonth().getValue()));
			dto.setHolidayDay(String.valueOf(date.getDayOfWeek().getValue()));
			dto.setIsActive(holiday.getIsActive());
			dto.setLocationCode(holidayId.getLocationCode());
			holidayDtos.add(dto);
		});
		return holidayDtos;
	}

	public List<HolidayDto> mapHolidaysFromObjectArray(List<Object[]> holidays) {
		List<HolidayDto> holidayDtos = new ArrayList<>();
		for (Object[] arr : holidays) {
			LocalDate date = LocalDate.parse((String) arr[3]);
			HolidayDto dto = new HolidayDto();
			dto.setHolidayId((String) arr[0]);
			dto.setHolidayDate(String.valueOf(date));
			dto.setLocationCode((String) arr[1]);
			dto.setHolidayName((String) arr[2]);
			dto.setLanguageCode((String) arr[4]);
			dto.setHolidayYear(String.valueOf(date.getYear()));
			dto.setHolidayMonth(String.valueOf(date.getMonth().getValue()));
			dto.setHolidayDay(String.valueOf(date.getDayOfWeek().getValue()));
			dto.setIsActive((boolean) arr[5]);
			holidayDtos.add(dto);
		}
		return holidayDtos;
	}

	public List<ReasonCategoryDto> reasonConverter(List<ReasonCategory> reasonCategories) {
		Objects.requireNonNull(reasonCategories, "list cannot be null");
		List<ReasonCategoryDto> reasonCategoryDtos = null;
		reasonCategoryDtos = reasonCategories.parallelStream()
				.map(reasonCategory -> new ReasonCategoryDto(reasonCategory.getCode(), reasonCategory.getName(),
						reasonCategory.getDescription(), reasonCategory.getLangCode(), reasonCategory.getIsActive(),
						reasonCategory.getIsDeleted(), mapAll(reasonCategory.getReasonList(), ReasonListDto.class)))
				.collect(Collectors.toList());

		return reasonCategoryDtos;

	}

	public List<LocationHierarchyDto> objectToDtoConverter(List<Object[]> locationList) {

		List<LocationHierarchyDto> locationHierarchyDtos = new ArrayList<>();
		for (Object[] object : locationList) {
			LocationHierarchyDto locationHierarchyDto = new LocationHierarchyDto();
			locationHierarchyDto.setLocationHierarchylevel((Short) object[0]);
			locationHierarchyDto.setLocationHierarchyName((String) object[1]);
			locationHierarchyDto.setIsActive((Boolean) object[2]);
			locationHierarchyDtos.add(locationHierarchyDto);
		}
		return locationHierarchyDtos;
	}

	public List<DeviceLangCodeDtypeDto> mapDeviceDto(List<Object[]> objects) {
		List<DeviceLangCodeDtypeDto> deviceLangCodeDtypeDtoList = new ArrayList<>();
		objects.forEach(arr -> {
			DeviceLangCodeDtypeDto deviceLangCodeDtypeDto = new DeviceLangCodeDtypeDto();
			deviceLangCodeDtypeDto.setId((String) arr[0]);
			deviceLangCodeDtypeDto.setName((String) arr[1]);
			deviceLangCodeDtypeDto.setMacAddress((String) arr[2]);
			deviceLangCodeDtypeDto.setSerialNum((String) arr[3]);
			deviceLangCodeDtypeDto.setIpAddress((String) arr[4]);
			deviceLangCodeDtypeDto.setDspecId((String) arr[5]);
			deviceLangCodeDtypeDto.setLangCode((String) arr[6]);
			deviceLangCodeDtypeDto.setActive((boolean) arr[7]);
			deviceLangCodeDtypeDto.setDeviceTypeCode((String) arr[8]);
			deviceLangCodeDtypeDtoList.add(deviceLangCodeDtypeDto);
		});
		return deviceLangCodeDtypeDtoList;
	}

	public List<DeviceTypeDto> mapDeviceTypeDto(List<DeviceType> deviceTypes) {
		List<DeviceTypeDto> deviceTypeDtoList = new ArrayList<>();
		DeviceTypeDto deviceTypeDto = new DeviceTypeDto();

		for (DeviceType deviceType : deviceTypes) {

			deviceTypeDto.setName(deviceType.getName());
			deviceTypeDto.setDescription(deviceType.getDescription());
			deviceTypeDto.setCode(deviceType.getCode());
			deviceTypeDto.setLangCode(deviceType.getLangCode());
		}
		deviceTypeDtoList.add(deviceTypeDto);
		return deviceTypeDtoList;
	}

	public List<DeviceSpecificationDto> mapDeviceSpecification(List<DeviceSpecification> deviceSpecificationList) {
		List<DeviceSpecificationDto> deviceSpecificationDtoList = new ArrayList<>();

		for (DeviceSpecification deviceSpecification : deviceSpecificationList) {
			DeviceSpecificationDto deviceSpecificationDto = new DeviceSpecificationDto();
			deviceSpecificationDto.setId(deviceSpecification.getId());
			deviceSpecificationDto.setName(deviceSpecification.getName());
			deviceSpecificationDto.setDescription(deviceSpecification.getDescription());
			deviceSpecificationDto.setLangCode(deviceSpecification.getLangCode());
			deviceSpecificationDto.setBrand(deviceSpecification.getBrand());
			deviceSpecificationDto.setDeviceTypeCode(deviceSpecification.getDeviceTypeCode());
			deviceSpecificationDto.setModel(deviceSpecification.getModel());
			deviceSpecificationDto.setMinDriverversion(deviceSpecification.getMinDriverversion());
			deviceSpecificationDto.setIsActive(deviceSpecification.getIsActive());
			deviceSpecificationDtoList.add(deviceSpecificationDto);
		}
		return deviceSpecificationDtoList;
	}

	public List<MachineDto> mapMachineListDto(List<Machine> machines) {
		List<MachineDto> machineDtoList = new ArrayList<>();

		for (Machine machine : machines) {
			MachineDto machineDto = mapMachineDto(machine);
			machineDtoList.add(machineDto);
		}
		return machineDtoList;
	}

	public MachineDto mapMachineDto(Machine machine) {
		MachineDto machineDto = new MachineDto();
		machineDto.setName(machine.getName());
		machineDto.setId(machine.getName());
		machineDto.setSerialNum(machine.getSerialNum());
		machineDto.setIsActive(machine.getIsActive());
		machineDto.setMachineSpecId(machine.getMachineSpecId());
		machineDto.setValidityDateTime(machine.getValidityDateTime());
		machineDto.setIpAddress(machine.getIpAddress());
		machineDto.setLangCode(machine.getLangCode());
		return machineDto;
	}

	public List<MachineSpecificationDto> mapMachineSpecification(List<MachineSpecification> list) {
		List<MachineSpecificationDto> machineSpecificationList = new ArrayList<>();
		for (MachineSpecification ms : list) {
			MachineSpecificationDto dto = new MachineSpecificationDto();
			dto.setId(ms.getId());
			dto.setBrand(ms.getBrand());
			dto.setDescription(ms.getDescription());
			dto.setIsActive(ms.getIsActive());
			dto.setLangCode(ms.getLangCode());
			dto.setMachineTypeCode(ms.getMachineTypeCode());
			dto.setMinDriverversion(ms.getMinDriverversion());
			dto.setModel(ms.getModel());
			dto.setName(ms.getName());
			machineSpecificationList.add(dto);
		}
		return machineSpecificationList;
	}

	public List<MachineTypeDto> mapMachineType(List<MachineType> list) {
		List<MachineTypeDto> machines = new ArrayList<>();
		for (MachineType mt : list) {
			MachineTypeDto dto = new MachineTypeDto();
			dto.setCode(mt.getCode());
			dto.setDescription(mt.getDescription());
			dto.setIsActive(mt.getIsActive());
			dto.setLangCode(mt.getLangCode());
			dto.setName(mt.getName());
			machines.add(dto);
		}
		return machines;
	}

	public List<TitleDto> maptitles(List<Title> titles) {
		List<TitleDto> list = new ArrayList<>();
		for (Title entity : titles) {
			TitleDto dto = new TitleDto();
			dto.setTitleCode(entity.getId().getCode());
			dto.setTitleName(entity.getTitleName());
			dto.setTitleDescription(entity.getTitleDescription());
			dto.setIsActive(entity.getIsActive());
			list.add(dto);
		}
		return list;
	}

	public List<DeviceTypeDto> mapDeviceTypes(List<Object[]> deviceTypes) {
		List<DeviceTypeDto> list = new ArrayList<>();
		for (Object[] arr : deviceTypes) {
			DeviceTypeDto dto = new DeviceTypeDto();
			dto.setCode((String) arr[0]);
			dto.setLangCode((String) arr[1]);
			dto.setName((String) arr[2]);
			dto.setDescription((String) arr[1]);
			dto.setIsActive((boolean) arr[4]);
			list.add(dto);
		}
		return list;
	}
}


package io.mosip.kernel.masterdata.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.converter.MachineHistroyConverter;
import io.mosip.kernel.masterdata.converter.RegistrationCenterConverter;
import io.mosip.kernel.masterdata.converter.RegistrationCenterHistoryConverter;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.DeviceSpecificationDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.LocationHierarchyDto;
import io.mosip.kernel.masterdata.dto.MachineHistoryDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.entity.DeviceSpecification;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.HolidayId;
import io.mosip.kernel.masterdata.entity.MachineHistory;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.RegistrationCenterHistory;

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

	public List<RegistrationCenterDto> mapRegistrationCenterHistory(List<RegistrationCenterHistory> list) {
		List<RegistrationCenterDto> responseDto = new ArrayList<>();
		list.forEach(p -> {
			RegistrationCenterDto dto = new RegistrationCenterDto();
			dataMapperImpl.map(p, dto, new RegistrationCenterHistoryConverter());
			dataMapperImpl.map(p, dto, true, null, null, true);
			responseDto.add(dto);
		});

		return responseDto;
	}
	
	public List<MachineHistoryDto> mapMachineHistory(List<MachineHistory> machineHistoryList) {
		List<MachineHistoryDto> responseDto = new ArrayList<>();
		machineHistoryList.forEach(p -> {
			MachineHistoryDto dto = new MachineHistoryDto();
			dataMapperImpl.map(p, dto, new MachineHistroyConverter());
			dataMapperImpl.map(p, dto, true, null, null, true);
			responseDto.add(dto);
		});

		return responseDto;
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
			HolidayId holidayId = holiday.getHolidayId();
			HolidayDto dto = new HolidayDto();
			dto.setHolidayId(String.valueOf(holidayId.getId()));
			dto.setHolidayDate(String.valueOf(date));
			dto.setHolidayName(holiday.getHolidayName());
			dto.setLanguageCode(holidayId.getLangCode());
			dto.setHolidayYear(String.valueOf(date.getYear()));
			dto.setHolidayMonth(String.valueOf(date.getMonth().getValue()));
			dto.setHolidayDay(String.valueOf(date.getDayOfWeek().getValue()));
			dto.setIsActive(holiday.getIsActive());
			holidayDtos.add(dto);
		});
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
	
	
								  
}

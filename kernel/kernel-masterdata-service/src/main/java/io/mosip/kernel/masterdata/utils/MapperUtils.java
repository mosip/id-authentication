
package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EmbeddedId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.converter.RegistrationCenterConverter;
import io.mosip.kernel.masterdata.converter.RegistrationCenterHierarchyLevelConverter;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.LocationHierarchyDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelDto;
import io.mosip.kernel.masterdata.entity.BaseEntity;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.id.HolidayID;

@Component
@SuppressWarnings("unchecked")
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

	// ----------------------------------------------------------------------------------------------------------------------------
	public <S, D> D mapNew(final S source, D destination) {
		if (!EmptyCheckUtils.isNullEmpty(source) && !EmptyCheckUtils.isNullEmpty(destination)) {
			mapValues(source, destination);
		}
		return destination;
	}

	public <S, D> D mapNew(final S source, Class<D> destinationClass) {
		Object destination = null;
		try {
			destination = destinationClass.newInstance();
		} catch (Exception e) {
			throw new DataAccessLayerException("KER-MSD-991",
					"Exception in mapping source object to class : " + e.getMessage(), e);
		}
		return (D) mapNew(source, destination);
	}

	public <S, D> List<D> mapAllNew(final Collection<S> sourceList, Class<D> destinationClass) {
		return sourceList.stream().map(entity -> mapNew(entity, destinationClass)).collect(Collectors.toList());
	}

	private <S, D> void mapValues(S source, D destination) {
		Field[] sourceFields = source.getClass().getDeclaredFields();
		boolean isIdMapped = false;
		boolean isSuperMapped = false;
		try {
			for (Field sfield : sourceFields) {

				sfield.setAccessible(true);
				if (!isIdMapped && sfield.isAnnotationPresent(EmbeddedId.class)) {
					setFieldValue(sfield.get(source), destination);
					sfield.setAccessible(false);
					isIdMapped = true;
				} else if (!isSuperMapped) {
					setBaseFieldValue(source, destination);
					isSuperMapped = true;
				} else {
					setFieldValue(source, destination);
					break;
				}
			}
		} catch (Exception e) {

			throw new DataAccessLayerException("KER-MSD-992",
					"Exception in mapping source object to destination object : " + e.getMessage(), e);
		}
	}

	private <S, D> void setBaseFieldValue(S source, D destination) {
		String sourceSupername = source.getClass().getSuperclass().getName();
		String destinationSupername = destination.getClass().getSuperclass().getName();
		String baseEntityClassName = BaseEntity.class.getName();
		if (sourceSupername.equals(baseEntityClassName)) {
			Field[] sourceFields = source.getClass().getSuperclass().getDeclaredFields();
			Field[] destinationFields = destination.getClass().getDeclaredFields();
			setFieldValues(source, destination, sourceFields, destinationFields);
			return;
		}
		if (destinationSupername.equals(baseEntityClassName)) {
			Field[] sourceFields = source.getClass().getDeclaredFields();
			Field[] destinationFields = destination.getClass().getSuperclass().getDeclaredFields();
			setFieldValues(source, destination, sourceFields, destinationFields);
		}

	}

	private <S, D> void setFieldValue(S source, D destination) {

		Field[] sourceFields = source.getClass().getDeclaredFields();
		Field[] destinationFields = destination.getClass().getDeclaredFields();

		setFieldValues(source, destination, sourceFields, destinationFields);

	}

	private <D, S> void setFieldValues(S source, D destination, Field[] sourceFields, Field[] destinationFields) {
		try {
			for (Field sfield : sourceFields) {
				if (Modifier.isStatic(sfield.getModifiers()) || Modifier.isFinal(sfield.getModifiers())) {
					continue;
				}
				sfield.setAccessible(true);
				for (Field dfield : destinationFields) {
					if (sfield.getName().equals(dfield.getName()) && sfield.getType().equals(dfield.getType())) {
						dfield.setAccessible(true);
						setFieldValue(source, destination, sfield, dfield);
						break;
					}
				}
			}
		} catch (Exception e) {

			throw new DataAccessLayerException("KER-MSD-993", e.getMessage(), e);
		}
	}

	public <D, S> void mapLocalDateTimeField(S source, D destination) {
		Field[] sourceFields = source.getClass().getDeclaredFields();
		Field[] destinationFields = destination.getClass().getDeclaredFields();
		try {
			sourceLoop: for (Field sfield : sourceFields) {
				sfield.setAccessible(true);
				for (Field dfield : destinationFields) {
					if (sfield.getName().equals(dfield.getName()) && sfield.getType().equals(dfield.getType())) {
						dfield.setAccessible(true);

						if (sfield.getType().isAssignableFrom(LocalDate.class)) {
							setFieldValue(source, destination, sfield, dfield);
							continue sourceLoop;
						}
						if (sfield.getType().isAssignableFrom(LocalTime.class)) {
							setFieldValue(source, destination, sfield, dfield);
							continue sourceLoop;
						}
						if (sfield.getType().isAssignableFrom(LocalDateTime.class)) {
							setFieldValue(source, destination, sfield, dfield);
							continue sourceLoop;
						}

					}
				}
			}
		} catch (Exception e) {
			throw new DataAccessLayerException("KER-MSD-994", "Error while mapping source object to destination", e);
		}
	}

	private <S, D> void setFieldValue(S source, D destination, Field ef, Field dtf) throws IllegalAccessException {
		dtf.set(destination, ef.get(source));
		dtf.setAccessible(false);
		ef.setAccessible(false);
	}
	// ----------------------------------------------------------------------------------------------------------------------------

	public List<RegistrationCenterHierarchyLevelDto> mapRegistrationCenterHierarchyLevel(
			List<RegistrationCenter> list) {
		List<RegistrationCenterHierarchyLevelDto> responseDto = new ArrayList<>();
		list.forEach(p -> {
			RegistrationCenterHierarchyLevelDto dto = new RegistrationCenterHierarchyLevelDto();
			dataMapperImpl.map(p, dto, new RegistrationCenterHierarchyLevelConverter());
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

	public List<RegistrationCenterDto> mapRegistrationCenter(RegistrationCenter entity) {
		List<RegistrationCenterDto> responseDto = new ArrayList<>();

		RegistrationCenterDto dto = new RegistrationCenterDto();
		dataMapperImpl.map(entity, dto, new RegistrationCenterConverter());
		dataMapperImpl.map(entity, dto, true, null, null, true);
		responseDto.add(dto);

		return responseDto;
	}

	public List<HolidayDto> mapHolidays(List<Holiday> holidays) {
		Objects.requireNonNull(holidays);
		List<HolidayDto> holidayDtos = new ArrayList<>();
		holidays.forEach(holiday -> {
			LocalDate date = holiday.getHolidayId().getHolidayDate();
			HolidayID holidayId = holiday.getHolidayId();
			HolidayDto dto = new HolidayDto();
			dto.setId(holiday.getId());
			dto.setHolidayDate(date);
			dto.setHolidayName(holiday.getHolidayName());
			dto.setLangCode(holidayId.getLangCode());
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

	//used for Object[]
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
			deviceLangCodeDtypeDto.setValidityEndDateTime(((Timestamp) arr[8]).toLocalDateTime());
			deviceLangCodeDtypeDto.setDeviceTypeCode((String) arr[9]);
			deviceLangCodeDtypeDtoList.add(deviceLangCodeDtypeDto);

		});
		return deviceLangCodeDtypeDtoList;
	}

	
	

}

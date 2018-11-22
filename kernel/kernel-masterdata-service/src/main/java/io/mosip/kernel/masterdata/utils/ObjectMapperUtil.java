
package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EmbeddedId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.core.exception.BaseCheckedException;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.PacketRejectionReasonResponseDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.HolidayId;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.ReasonList;

@Component
public class ObjectMapperUtil {

	@Autowired
	private DataMapper dataMapper;

	public <E, D> D map(final E entity, D object) {
		dataMapper.map(entity, object, true, null, null, true);
		return object;
	}

	public <D, T> D map(final T entity, Class<D> outCLass) {
		return dataMapper.map(entity, outCLass, true, null, null, true);
	}

	public <D, T> List<D> mapAll(final Collection<T> entityList, Class<D> outCLass) {
		return entityList.stream().map(entity -> map(entity, outCLass)).collect(Collectors.toList());
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

	public List<ReasonCategory> reasonConvertDtoToEntity(PacketRejectionReasonResponseDto reasonCategories) {

		Objects.requireNonNull(reasonCategories, "list cannot be null");
		List<ReasonCategory> reasonCategoryDtos = null;

		reasonCategoryDtos = reasonCategories.getReasonCategories().stream()
				.map(reasonCategory -> new ReasonCategory(reasonCategory.getCode(), reasonCategory.getName(),
						reasonCategory.getDescription(), reasonCategory.getLangCode(),
						reasonListDtoToEntity(reasonCategory.getReasonList()), true, false, "system", "system",
						LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()))
				.collect(Collectors.toList());

		return reasonCategoryDtos;

	}

	public List<ReasonList> reasonListDtoToEntity(List<ReasonListDto> reasonListDto) {
		Objects.requireNonNull(reasonListDto, "list cannot be null");
		List<ReasonList> reasonLists = null;
		reasonLists = reasonListDto.stream()
				.map(reasonDto -> new ReasonList(reasonDto.getCode(), reasonDto.getRsnCatCode(),
						reasonDto.getLangCode(), reasonDto.getName(), reasonDto.getDescription(), true, false, "system",
						"system", LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now()))
				.collect(Collectors.toList());
		return reasonLists;

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

	/**
	 * 
	 * @param dto
	 * @param entity
	 * @return return the entity object
	 * @throws BaseCheckedException
	 */
	public <D, S> D mapDtoToEntity(final S dto, Class<D> entity) throws BaseCheckedException {
		try {

			Field allField[] = entity.getDeclaredFields();

			Object embeddedId = null;

			for (Field f : allField) {
				if (f.isAnnotationPresent(EmbeddedId.class)) {
					// copy dto values to embedded id fields
					// make sure dto field name equal to embedded id fields name
					embeddedId = map(dto, f.getType());
					break;// not required to check any other
				}
			}

			if (Objects.isNull(embeddedId)) {
				throw new NullPointerException("EmbeddedId is null. Check Dto.");
			}

			D destination = entity.getConstructor().newInstance();// important to create object of entity type

			map(embeddedId, destination);// adding embedded id to entity object

			map(dto, destination);// adding values other then embedded id to entity object

			// adding values meta data
			setMetaDataValues(dto, destination);

			return destination;
		} catch (Exception e) {
			throw new BaseCheckedException("M1048916", "Error while mapping dto to entity", e);
		}

	}

	private <D, S> void setMetaDataValues(final S dto, D destination)
			throws NoSuchFieldException, IllegalAccessException {
		if (destination.getClass().getSuperclass() != null) {
			Field baseFields[] = destination.getClass().getSuperclass().getDeclaredFields();
			for (Field field : baseFields) {
				field.setAccessible(true);

				switch (field.getName()) {

				case "isActive":
					if (!setMetadataByUser(dto, destination, field)) {
						field.set(destination, Boolean.TRUE);
					}
					break;
				case "createdBy":
					if (!setMetadataByUser(dto, destination, field)) {
						field.set(destination, "bantiz");
					}
					break;
				case "createdtimes":
					if (!setMetadataByUser(dto, destination, field)) {
						field.set(destination, LocalDateTime.now());
					}
					break;
				case "updatedBy":
					if (!setMetadataByUser(dto, destination, field)) {
						// field.set(destination, "bantiz");
					}
					break;
				case "updatedtimes":
					if (!setMetadataByUser(dto, destination, field)) {
						// field.set(destination, LocalDateTime.now());
					}
					break;
				case "isDeleted":
					if (!setMetadataByUser(dto, destination, field)) {
						// field.set(destination, Boolean.FALSE);
					}
					break;
				case "deletedtimes":
					if (!setMetadataByUser(dto, destination, field)) {
						// field.set(destination, LocalDateTime.now());
					}
					break;

				}

				field.setAccessible(false);
			}
		}
	}

	private <S, D> boolean setMetadataByUser(final S dto, D destination, Field field)
			throws NoSuchFieldException, IllegalAccessException {
		Field temp = null;
		try {

			temp = dto.getClass().getDeclaredField(field.getName());

		} catch (NoSuchFieldException e) {
			return false;
		}

		if (Objects.nonNull(temp)) {
			temp.setAccessible(true);
			if (temp.get(dto) != null) {
				field.set(destination, temp.get(dto));
				temp.setAccessible(false);
				return true;
			}
		}

		return false;
	}

}

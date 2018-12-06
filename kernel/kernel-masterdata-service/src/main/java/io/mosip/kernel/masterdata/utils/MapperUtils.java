
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
import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.converter.RegistrationCenterConverter;
import io.mosip.kernel.masterdata.converter.RegistrationCenterHierarchyLevelConverter;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.DeviceTypeDto;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.LocationHierarchyDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.RegistrationCenterHierarchyLevelDto;
import io.mosip.kernel.masterdata.entity.BaseEntity;
import io.mosip.kernel.masterdata.entity.DeviceType;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.entity.id.HolidayID;

/**
 * MapperUtils class provides methods to map or copy values from source object
 * to destination object.
 * 
 * @author Bal Vikash Sharma
 * @since 1.0.0
 * @see MapperUtils
 *
 */
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

	/*
	 * #############Public method used for mapping################################
	 */

	/**
	 * This method map the values from <code>source</code> to
	 * <code>destination</code> if name and type of the fields inside the given
	 * parameters are same.If any of the parameters are <code>null</code> this
	 * method return <code>null</code>.
	 * 
	 * @param source
	 *            which value is going to be mapped
	 * @param destination
	 *            where values is going to be mapped
	 * @return the <code>destination</code> object
	 */
	public <S, D> D mapNew(final S source, D destination) {
		if (!EmptyCheckUtils.isNullEmpty(source) && !EmptyCheckUtils.isNullEmpty(destination)) {
			mapValues(source, destination);
		}
		return destination;
	}

	/**
	 * This method takes <code>source</code> and <code>destinationClass</code>, take
	 * all values from source and create an object of <code>destinationClass</code>
	 * and map all the values from source to destination if field name and type is
	 * same.
	 * 
	 * @param source
	 *            which value is going to be mapped
	 * @param destinationClass
	 *            where values is going to be mapped
	 * @return the object of <code>destinationClass</code>
	 * @throws DataAccessLayerException
	 *             if exception occur during creating of
	 *             <code>destinationClass</code> object
	 */
	public <S, D> D mapNew(final S source, Class<D> destinationClass) {
		Object destination = null;
		try {
			destination = destinationClass.newInstance();
		} catch (Exception e) {
			throw new DataAccessLayerException("KER-MSD-991", "Exception in creating destinationClass object", e);
		}
		return (D) mapNew(source, destination);
	}

	/**
	 * This method takes <code>sourceList</code> and <code>destinationClass</code>,
	 * take all values from source and create an object of
	 * <code>destinationClass</code> and map all the values from source to
	 * destination if field name and type is same.
	 * 
	 * @param sourceList
	 *            which value is going to be mapped
	 * @param destinationClass
	 *            where values is going to be mapped
	 * @return list of destinationClass objects
	 * @throws DataAccessLayerException
	 *             if exception occur during creating of
	 *             <code>destinationClass</code> object
	 */
	public <S, D> List<D> mapAllNew(final Collection<S> sourceList, Class<D> destinationClass) {
		return sourceList.stream().map(entity -> mapNew(entity, destinationClass)).collect(Collectors.toList());
	}

	/**
	 * This method map values of <code>source</code> object to
	 * <code>destination</code> object. It will map field values having same name
	 * and same type for the fields. It will not map any field which is static or
	 * final.It will simply ignore those values.
	 * 
	 * @param source
	 *            is any object which should not be null and have data which is
	 *            going to be copied
	 * @param destination
	 *            is an object in which source field values is going to be matched
	 * 
	 * @throws DataAccessLayerException
	 *             if error raised during mapping values
	 */
	public <S, D> void mapFieldValues(S source, D destination) {

		Field[] sourceFields = source.getClass().getDeclaredFields();
		Field[] destinationFields = destination.getClass().getDeclaredFields();

		mapFieldValues(source, destination, sourceFields, destinationFields);

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

	/*
	 * #############Private method used for mapping################################
	 */

	private <S, D> void mapValues(S source, D destination) {
		mapFieldValues(source, destination);// this method simply map values if field name and type are same

		if (source.getClass().isAnnotationPresent(Entity.class)) {
			mapEntityToDto(source, destination);
		} else {
			mapDtoToEntity(source, destination);
		}
	}

	private <S, D> void mapDtoToEntity(S source, D destination) {
		Field[] fields = destination.getClass().getDeclaredFields();
		setBaseFieldValue(source, destination);// map super class values
		for (Field field : fields) {
			/**
			 * Map DTO matching field values to super class field values
			 */
			if (field.isAnnotationPresent(EmbeddedId.class)) {
				try {
					Object id = field.getType().newInstance();
					mapFieldValues(source, id);
					field.setAccessible(true);
					field.set(destination, id);
					field.setAccessible(false);
					break;
				} catch (Exception e) {
					throw new DataAccessLayerException("KER-MSD-000", "Error while mapping Embedded Id fields", e);
				}
			}
		}
	}

	private <S, D> void mapEntityToDto(S source, D destination) {
		Field[] sourceFields = source.getClass().getDeclaredFields();
		/*
		 * Here source is a Entity so we need to take values from Entity object and set
		 * the matching fields in the destination object mostly an DTO.
		 */
		try {
			boolean isIdMapped = false;// a flag to check if there any composite key is present and is mapped
			boolean isSuperMapped = false;// a flag to check is class extends the BaseEntity and is mapped
			for (Field sfield : sourceFields) {
				sfield.setAccessible(true);// mark accessible true because fields my be private, for safety
				if (!isIdMapped && sfield.isAnnotationPresent(EmbeddedId.class)) {
					/**
					 * Map the composite key values from source to destination if field name is same
					 */
					/**
					 * Take the field and get the composite key object and map all values to
					 * destination object
					 */
					mapFieldValues(sfield.get(source), destination);
					sfield.setAccessible(false);
					isIdMapped = true;// set flag so no need to check and map again
				} else if (!isSuperMapped) {
					setBaseFieldValue(source, destination);// this method check whether source is entity or destination
															// and maps values accordingly
					isSuperMapped = true;
				}
			}
		} catch (Exception e) {

			throw new DataAccessLayerException("KER-MSD-992",
					"Exception in mapping source object : " + source.getClass().getName() + " to destination object : "
							+ destination.getClass().getName() + e.getMessage(),
					e);
		}
	}

	private <S, D> void setBaseFieldValue(S source, D destination) {

		String sourceSupername = source.getClass().getSuperclass().getName();// super class of source object
		String destinationSupername = destination.getClass().getSuperclass().getName();// super class of destination
																						// object
		String baseEntityClassName = BaseEntity.class.getName();// base entity fully qualified name

		// if source is an entity
		if (sourceSupername.equals(baseEntityClassName)) {
			Field[] sourceFields = source.getClass().getSuperclass().getDeclaredFields();
			Field[] destinationFields = destination.getClass().getDeclaredFields();
			mapFieldValues(source, destination, sourceFields, destinationFields);
			return;
		}
		// if destination is an entity
		if (destinationSupername.equals(baseEntityClassName)) {
			Field[] sourceFields = source.getClass().getDeclaredFields();
			Field[] destinationFields = destination.getClass().getSuperclass().getDeclaredFields();
			mapFieldValues(source, destination, sourceFields, destinationFields);
		}

	}

	private <D, S> void mapFieldValues(S source, D destination, Field[] sourceFields, Field[] destinationFields) {
		try {
			for (Field sfield : sourceFields) {
				// Do not set values either static or final
				if (Modifier.isStatic(sfield.getModifiers()) || Modifier.isFinal(sfield.getModifiers())) {
					continue;
				}

				// make field accessible possibly private
				sfield.setAccessible(true);

				for (Field dfield : destinationFields) {

					Class<?> sourceType = sfield.getType();
					Class<?> destinationType = dfield.getType();

					// map only those field whose name and type is same
					if (sfield.getName().equals(dfield.getName()) && sourceType.equals(destinationType)) {

						// for normal field values
						dfield.setAccessible(true);
						setFieldValue(source, destination, sfield, dfield);
						break;
					}
				}
			}
		} catch (Exception e) {

			throw new DataAccessLayerException("KER-MSD-993", "Exception raised while mapping values form "
					+ source.getClass().getName() + " to " + destination.getClass().getName(), e);
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
			deviceLangCodeDtypeDto.setValidityEndDateTime(((Timestamp) arr[8]).toLocalDateTime());
			deviceLangCodeDtypeDto.setDeviceTypeCode((String) arr[9]);
			deviceLangCodeDtypeDtoList.add(deviceLangCodeDtypeDto);

		});
		return deviceLangCodeDtypeDtoList;
	}

	public List<DeviceTypeDto> mapDeviceTypeDto(List<DeviceType> deviceTypes) {
		List<DeviceTypeDto> deviceTypeDtoList = new ArrayList<>();

		for (DeviceType deviceType : deviceTypes) {
			DeviceTypeDto deviceTypeDto = new DeviceTypeDto();
			deviceTypeDto.setName(deviceType.getName());
			deviceTypeDto.setDescription(deviceType.getDescription());
			deviceTypeDto.setCode(deviceType.getCode());
			deviceTypeDto.setLangCode(deviceType.getLangCode());
			deviceTypeDtoList.add(deviceTypeDto);
		}

		return deviceTypeDtoList;
	}

	/*
	 * public List<MachineDto> mapMachineListDto(List<Machine> machines) {
	 * List<MachineDto> machineDtoList = new ArrayList<>();
	 * 
	 * for (Machine machine : machines) { MachineDto machineDto =
	 * mapMachineDto(machine); machineDtoList.add(machineDto); } return
	 * machineDtoList; }
	 * 
	 * public MachineDto mapMachineDto(Machine machine) { MachineDto machineDto =
	 * new MachineDto(); machineDto.setName(machine.getName());
	 * machineDto.setId(machine.getId());
	 * machineDto.setSerialNum(machine.getSerialNum());
	 * machineDto.setIsActive(machine.getIsActive());
	 * machineDto.setMachineSpecId(machine.getMachineSpecId());
	 * machineDto.setValidityDateTime(machine.getValidityDateTime());
	 * machineDto.setIpAddress(machine.getIpAddress());
	 * machineDto.setLangCode(machine.getLangCode());
	 * machineDto.setMacAddress(machine.getMacAddress()); return machineDto; }
	 */

}

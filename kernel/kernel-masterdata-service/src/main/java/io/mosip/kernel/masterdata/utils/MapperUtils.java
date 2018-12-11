
package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.DeviceLangCodeDtypeDto;
import io.mosip.kernel.masterdata.dto.HolidayDto;
import io.mosip.kernel.masterdata.dto.ReasonCategoryDto;
import io.mosip.kernel.masterdata.dto.ReasonListDto;
import io.mosip.kernel.masterdata.dto.getresponse.LocationHierarchyDto;
import io.mosip.kernel.masterdata.entity.BaseEntity;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.ReasonCategory;
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

	private MapperUtils() {
		super();
	}

	private static final String UTC_DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	private static final String SOURCE_NULL_MESSAGE = "source should not be null";
	private static final String DESTINATION_NULL_MESSAGE = "destination should not be null";

	public static LocalDateTime parseToLocalDateTime(String dateTime) {
		return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern(UTC_DATETIME_PATTERN));
	}

	/*
	 * #############Public method used for mapping################################
	 */

	/**
	 * This method map the values from <code>source</code> to
	 * <code>destination</code> if name and type of the fields inside the given
	 * parameters are same.If any of the parameters are <code>null</code> this
	 * method return <code>null</code>.This method internally check whether the
	 * source or destinationClass is DTO or an Entity type and map accordingly. If
	 * any {@link Collection} type or Entity type field is their then only matched
	 * name fields value will be set but not the embedded IDs and super class
	 * values.
	 * 
	 * @param source
	 *            which value is going to be mapped
	 * @param destination
	 *            where values is going to be mapped
	 * @return the <code>destination</code> object
	 * @throws NullPointerException
	 *             if either <code>source</code> or <code>destination</code> is null
	 */
	public static <S, D> D map(final S source, D destination) {
		Objects.requireNonNull(source, SOURCE_NULL_MESSAGE);
		Objects.requireNonNull(destination, DESTINATION_NULL_MESSAGE);
		try {
			mapValues(source, destination);
		} catch (IllegalAccessException | InstantiationException e) {
			throw new DataAccessLayerException("KER-MSD-991", "Exception in mapping vlaues from source : "
					+ source.getClass().getName() + " to destination : " + destination.getClass().getName(), e);
		}
		return destination;
	}

	/**
	 * This method takes <code>source</code> and <code>destinationClass</code>, take
	 * all values from source and create an object of <code>destinationClass</code>
	 * and map all the values from source to destination if field name and type is
	 * same.This method internally check whether the source or destinationClass is
	 * DTO or an Entity type and map accordingly.If any {@link Collection} type or
	 * Entity type field is their then only matched name fields value will be set
	 * but not the embedded IDs and super class values.
	 * 
	 * @param <S>
	 *            is a type parameter
	 * @param <D>
	 *            is a type parameter
	 * @param source
	 *            which value is going to be mapped
	 * @param destinationClass
	 *            where values is going to be mapped
	 * @return the object of <code>destinationClass</code>
	 * @throws DataAccessLayerException
	 *             if exception occur during creating of
	 *             <code>destinationClass</code> object
	 * @throws NullPointerException
	 *             if either <code>source</code> or <code>destinationClass</code> is
	 *             null
	 */
	public static <S, D> D map(final S source, Class<D> destinationClass) {
		Objects.requireNonNull(source, SOURCE_NULL_MESSAGE);
		Objects.requireNonNull(destinationClass, "destination class should not be null");
		Object destination = null;
		try {
			destination = destinationClass.newInstance();
		} catch (Exception e) {
			throw new DataAccessLayerException("KER-MSD-991", "Exception in mapping vlaues from source : "
					+ source.getClass().getName() + " to destination : " + destinationClass.getClass().getName(), e);
		}
		return (D) map(source, destination);
	}

	/**
	 * This method takes <code>sourceList</code> and <code>destinationClass</code>,
	 * take all values from source and create an object of
	 * <code>destinationClass</code> and map all the values from source to
	 * destination if field name and type is same.
	 * 
	 * @param <S>
	 *            is a type parameter
	 * 
	 * @param <D>
	 *            is a type parameter
	 * @param sourceList
	 *            which value is going to be mapped
	 * @param destinationClass
	 *            where values is going to be mapped
	 * @return list of destinationClass objects
	 * @throws DataAccessLayerException
	 *             if exception occur during creating of
	 *             <code>destinationClass</code> object
	 * @throws NullPointerException
	 *             if either <code>sourceList</code> or
	 *             <code>destinationClass</code> is null
	 */
	public static <S, D> List<D> mapAll(final Collection<S> sourceList, Class<D> destinationClass) {
		Objects.requireNonNull(sourceList, "sourceList should not be null");
		Objects.requireNonNull(destinationClass, "destinationClass should not be null");
		return sourceList.stream().map(entity -> map(entity, destinationClass)).collect(Collectors.toList());
	}

	/**
	 * This method map values of <code>source</code> object to
	 * <code>destination</code> object. It will map field values having same name
	 * and same type for the fields. It will not map any field which is static or
	 * final.It will simply ignore those values.
	 * 
	 * @param <S>
	 *            is a type parameter
	 * 
	 * @param <D>
	 *            is a type parameter
	 * @param source
	 *            is any object which should not be null and have data which is
	 *            going to be copied
	 * @param destination
	 *            is an object in which source field values is going to be matched
	 * 
	 * @throws DataAccessLayerException
	 *             if error raised during mapping values
	 * @throws NullPointerException
	 *             if either <code>source</code> or <code>destination</code> is null
	 */
	public static <S, D> void mapFieldValues(S source, D destination) {

		Objects.requireNonNull(source, SOURCE_NULL_MESSAGE);
		Objects.requireNonNull(destination, DESTINATION_NULL_MESSAGE);
		Field[] sourceFields = source.getClass().getDeclaredFields();
		Field[] destinationFields = destination.getClass().getDeclaredFields();

		mapFieldValues(source, destination, sourceFields, destinationFields);

	}

	/*
	 * #############Private method used for mapping################################
	 */

	private static <S, D> void mapValues(S source, D destination)
			throws IllegalAccessException, InstantiationException {
		mapFieldValues(source, destination);// this method simply map values if field name and type are same

		if (source.getClass().isAnnotationPresent(Entity.class)) {
			mapEntityToDto(source, destination);
		} else {
			mapDtoToEntity(source, destination);
		}
	}

	private static <S, D> void mapDtoToEntity(S source, D destination)
			throws InstantiationException, IllegalAccessException {
		Field[] fields = destination.getClass().getDeclaredFields();
		setBaseFieldValue(source, destination);// map super class values
		for (Field field : fields) {
			/**
			 * Map DTO matching field values to super class field values
			 */
			if (field.isAnnotationPresent(EmbeddedId.class)) {
				Object id = field.getType().newInstance();
				mapFieldValues(source, id);
				field.setAccessible(true);
				field.set(destination, id);
				field.setAccessible(false);
				break;
			}
		}
	}

	private static <S, D> void mapEntityToDto(S source, D destination) throws IllegalAccessException {
		Field[] sourceFields = source.getClass().getDeclaredFields();
		/*
		 * Here source is a Entity so we need to take values from Entity object and set
		 * the matching fields in the destination object mostly an DTO.
		 */
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
	}

	private static <S, D> void setBaseFieldValue(S source, D destination) {

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

	private static <D, S> void mapFieldValues(S source, D destination, Field[] sourceFields,
			Field[] destinationFields) {
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

	private static <S, D> void setFieldValue(S source, D destination, Field ef, Field dtf)
			throws IllegalAccessException {
		dtf.set(destination, ef.get(source));
		dtf.setAccessible(false);
		ef.setAccessible(false);
	}
	// ----------------------------------------------------------------------------------------------------------------------------

	public static List<HolidayDto> mapHolidays(List<Holiday> holidays) {
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

	public static List<ReasonCategoryDto> reasonConverter(List<ReasonCategory> reasonCategories) {
		Objects.requireNonNull(reasonCategories, "list cannot be null");
		List<ReasonCategoryDto> reasonCategoryDtos = null;
		reasonCategoryDtos = reasonCategories.parallelStream()
				.map(reasonCategory -> new ReasonCategoryDto(reasonCategory.getCode(), reasonCategory.getName(),
						reasonCategory.getDescription(), reasonCategory.getLangCode(), reasonCategory.getIsActive(),
						mapAll(reasonCategory.getReasonList(), ReasonListDto.class)))
				.collect(Collectors.toList());

		return reasonCategoryDtos;

	}

	public static List<LocationHierarchyDto> objectToDtoConverter(List<Object[]> locationList) {

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

	public static List<DeviceLangCodeDtypeDto> mapDeviceDto(List<Object[]> objects) {
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

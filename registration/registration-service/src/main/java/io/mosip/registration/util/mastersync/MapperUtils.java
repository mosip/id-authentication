
package io.mosip.registration.util.mastersync;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.entity.mastersync.MasterSyncBaseEntity;

/**
 * MapperUtils class provides methods to map or copy values from source object
 * to destination object.
 * 
 * @author Sreekar Chukka
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

	private static final String SOURCE_NULL_MESSAGE = "source should not be null";
	private static final String DESTINATION_NULL_MESSAGE = "destination should not be null";

	/**
	 * This flag is used to restrict copy null values.
	 */
	private static Boolean mapNullValues = Boolean.TRUE;

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
	 * @param               <S> is a type parameter
	 * @param               <D> is a type parameter
	 * @param source        which value is going to be mapped
	 * @param destination   where values is going to be mapped
	 * @param mapNullValues by default marked as true so, it will map null values
	 *                      but if marked as false then null values will be ignored
	 * @return the <code>destination</code> object
	 * @throws NullPointerException if either <code>source</code> or
	 *                              <code>destination</code> is null
	 */
	public static <S, D> D map(final S source, D destination, Boolean mapNullValues) {
		MapperUtils.mapNullValues = mapNullValues;
		return map(source, destination);
	}

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
	 * @param             <S> is a type parameter
	 * @param             <D> is a type parameter
	 * @param source      which value is going to be mapped
	 * @param destination where values is going to be mapped
	 * @return the <code>destination</code> object
	 * @throws NullPointerException if either <code>source</code> or
	 *                              <code>destination</code> is null
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
	 * @param                  <S> is a type parameter
	 * @param                  <D> is a type parameter
	 * @param source           which value is going to be mapped
	 * @param destinationClass where values is going to be mapped
	 * @return the object of <code>destinationClass</code>
	 * @throws DataAccessLayerException if exception occur during creating of
	 *                                  <code>destinationClass</code> object
	 * @throws NullPointerException     if either <code>source</code> or
	 *                                  <code>destinationClass</code> is null
	 */
	public static <S, D> D map(final S source, Class<D> destinationClass) {
		Objects.requireNonNull(source, SOURCE_NULL_MESSAGE);
		Objects.requireNonNull(destinationClass, "destination class should not be null");
		Object destination = null;
		try {
			destination = destinationClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new DataAccessLayerException("KER-MSD-991", "Exception in mapping vlaues from source : "
					+ source.getClass().getName() + " to destination : " + destinationClass.getClass().getName(), e);
		}
		return (D) map(source, destination);
	}

	/**
	 * This method map values of <code>source</code> object to
	 * <code>destination</code> object. It will map field values having same name
	 * and same type for the fields. It will not map any field which is static or
	 * final.It will simply ignore those values.
	 * 
	 * @param             <S> is a type parameter
	 * 
	 * @param             <D> is a type parameter
	 * @param source      is any object which should not be null and have data which
	 *                    is going to be copied
	 * @param destination is an object in which source field values is going to be
	 *                    matched
	 * 
	 * @throws DataAccessLayerException if error raised during mapping values
	 * @throws NullPointerException     if either <code>source</code> or
	 *                                  <code>destination</code> is null
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

	/**
	 * Map values from source object to destination object.
	 * 
	 * @param source      which value is going to be mapped
	 * @param destination where values is going to be mapped
	 * @throws InstantiationException if not able to create instance of field having
	 *                                annotation {@link EmbeddedId}
	 * @throws IllegalAccessException if provided fields are not accessible
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

	/**
	 * This method map source DTO to a class object which extends {@link BaseEntity}
	 * 
	 * @param source      which value is going to be mapped
	 * @param destination where values is going to be mapped
	 * @throws InstantiationException if not able to create instance of field having
	 *                                annotation {@link EmbeddedId}
	 * @throws IllegalAccessException if provided fields are not accessible
	 */
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

	/**
	 * Map source which extends {@link BaseEntity} to a DTO object.
	 * 
	 * @param source      which value is going to be mapped
	 * @param destination where values is going to be mapped
	 * @throws IllegalAccessException if provided fields are not accessible
	 */
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

	/**
	 * Map values from {@link BaseEntity} class source object to destination or vice
	 * versa.
	 * 
	 * @param source      which value is going to be mapped
	 * @param destination where values is going to be mapped
	 */
	private static <S, D> void setBaseFieldValue(S source, D destination) {

		String sourceSupername = source.getClass().getSuperclass().getName();// super class of source object
		String destinationSupername = destination.getClass().getSuperclass().getName();// super class of destination
																						// object
		String baseEntityClassName = MasterSyncBaseEntity.class.getName();// base entity fully qualified name

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

	/**
	 * Map values from source field to destination.
	 * 
	 * @param source      which value is going to be mapped
	 * @param destination where values is going to be mapped
	 * @param sf          source fields
	 * @param dtf         destination fields
	 */
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
		} catch (IllegalAccessException e) {

			throw new DataAccessLayerException("KER-MSD-993", "Exception raised while mapping values form "
					+ source.getClass().getName() + " to " + destination.getClass().getName(), e);
		}
	}

	/**
	 * Take value from source field and insert value into destination field.
	 * 
	 * @param source      which value is going to be mapped
	 * @param destination where values is going to be mapped
	 * @param sf          source fields
	 * @param dtf         destination fields
	 * @throws IllegalAccessException if provided fields are not accessible
	 */
	private static <S, D> void setFieldValue(S source, D destination, Field sf, Field dtf)
			throws IllegalAccessException {
		// check whether user wants to map null values into destination object or not
		if (!mapNullValues && EmptyCheckUtils.isNullEmpty(sf.get(source))) {
			return;
		}
		dtf.set(destination, sf.get(source));
		dtf.setAccessible(false);
		sf.setAccessible(false);
	}

}

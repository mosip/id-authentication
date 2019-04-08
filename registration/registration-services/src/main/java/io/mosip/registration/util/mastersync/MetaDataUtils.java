package io.mosip.registration.util.mastersync;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.entity.RegistrationCommonFields;

/**
 * MetaDataUtils class provide methods to copy values from DTO to entity along
 * with that it create some meta data which is required before an entity to be
 * saved into database.
 * 
 * @author Sreekar Chukka
 * @since 1.0.0
 * @see MapperUtils
 *
 */
@Component
@SuppressWarnings("unchecked")
public class MetaDataUtils {

	private MetaDataUtils() {
		super();
	}

	/**
	 * This method takes <code>source</code> object like an DTO and a class which
	 * must extends {@link RegistrationCommonFields} and map all values from DTO object to the
	 * <code>destination</code> object and return it.
	 *
	 * @param <S> the generic type
	 * @param <D> the generic type
	 * @param source        is the source
	 * @param destination   is the destination
	 * @param mapNullvalues if marked as false then field inside source which are
	 *                      null will not be mapped into destination
	 * @return an entity class which extends {@link RegistrationCommonFields}
	 * @throws DataAccessLayerException if any error occurs while mapping values
	 * @see MapperUtils#map(Object, Object, Boolean)
	 */
	public static <S, D extends RegistrationCommonFields> D setUpdateMetaData(final S source, D destination,
			Boolean mapNullvalues) {

		String contextUser;
		if(SessionContext.isSessionContextAvailable()) {
			contextUser = SessionContext.userContext().getUserId();
		}else {
			contextUser=RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
		}

		D entity = MapperUtils.map(source, destination, mapNullvalues);

		setUpdatedDateTime(contextUser, entity);
		return entity;
	}

	/**
	 * This method takes <code>source</code> object like an DTO and a class which
	 * must extends {@link RegistrationCommonFields} and map all values from DTO object to the
	 * <code>destinationClass</code> object and return it.
	 * 
	 * @param                  <T> is a type parameter
	 * @param                  <D> is a type parameter
	 * @param source           is the source
	 * @param destinationClass is the destination class
	 * @return an entity class which extends {@link RegistrationCommonFields}
	 * @throws DataAccessLayerException if any error occurs while mapping values
	 * @see MapperUtils#map(Object, Class)
	 */
	public static <T, D extends RegistrationCommonFields> D setCreateMetaData(final T source,
			Class<? extends RegistrationCommonFields> destinationClass) {
		String contextUser;
		if(SessionContext.isSessionContextAvailable()) {
			contextUser = SessionContext.userContext().getUserId();
		}else {
			contextUser=RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
		}

		D entity = (D) MapperUtils.map(source, destinationClass);

		setCreatedDateTime(contextUser, entity);
		return entity;
	}

	/**
	 * Sets the create meta data.
	 *
	 * @param <T> the generic type
	 * @param <D> the generic type
	 * @param dtoList the dto list
	 * @param entityClass the entity class
	 * @return the list
	 */
	public static <T, D extends RegistrationCommonFields> List<D> setCreateMetaData(final Collection<T> dtoList,
			Class<? extends RegistrationCommonFields> entityClass) {

		String contextUser;
		if (SessionContext.isSessionContextAvailable()) {
			contextUser = SessionContext.userContext().getUserId();
		} else {
			contextUser = RegistrationConstants.JOB_TRIGGER_POINT_SYSTEM;
		}

		List<D> entities = new ArrayList<>();

		if (null != dtoList && !dtoList.isEmpty()) {
			for (T dto : dtoList) {
				D entity = (D) MapperUtils.map(dto, entityClass);
				setCreatedDateTime(contextUser, entity);
				entities.add(entity);
			}
		}

		return entities;

	}

	/**
	 * Sets the created date time.
	 *
	 * @param <D> the generic type
	 * @param contextUser the context user
	 * @param entity the entity
	 */
	private static <D extends RegistrationCommonFields> void setCreatedDateTime(String contextUser, D entity) {
		entity.setCrDtime(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		entity.setCrBy(contextUser);
	}

	/**
	 * Sets the updated date time.
	 *
	 * @param <D> the generic type
	 * @param contextUser the context user
	 * @param entity the entity
	 */
	private static <D extends RegistrationCommonFields> void setUpdatedDateTime(String contextUser, D entity) {
		entity.setUpdDtimes(Timestamp.valueOf(DateUtils.getUTCCurrentDateTime()));
		entity.setUpdBy(contextUser);
	}

}

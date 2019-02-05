package io.mosip.registration.util.mastersync;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.entity.mastersync.MasterSyncBaseEntity;

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
	 * must extends {@link BaseEntity} and map all values from DTO object to the
	 * <code>destination</code> object and return it.
	 * 
	 * @param               <S> is a type parameter
	 * @param               <D> is a type parameter
	 * @param source        is the source
	 * @param destination   is the destination
	 * @param mapNullvalues if marked as false then field inside source which are
	 *                      null will not be mapped into destination
	 * @return an entity class which extends {@link BaseEntity}
	 * @throws DataAccessLayerException if any error occurs while mapping values
	 * @see MapperUtils#map(Object, Object, Boolean)
	 */
	public static <S, D extends MasterSyncBaseEntity> D setUpdateMetaData(final S source, D destination,
			Boolean mapNullvalues) {

		String contextUser = SessionContext.getSessionContext().getUserContext().getUserId();

		D entity = MapperUtils.map(source, destination, mapNullvalues);

		setUpdatedDateTime(contextUser, entity);
		return entity;
	}

	/**
	 * This method takes <code>source</code> object like an DTO and a class which
	 * must extends {@link BaseEntity} and map all values from DTO object to the
	 * <code>destinationClass</code> object and return it.
	 * 
	 * @param                  <T> is a type parameter
	 * @param                  <D> is a type parameter
	 * @param source           is the source
	 * @param destinationClass is the destination class
	 * @return an entity class which extends {@link BaseEntity}
	 * @throws DataAccessLayerException if any error occurs while mapping values
	 * @see MapperUtils#map(Object, Class)
	 */
	public static <T, D extends MasterSyncBaseEntity> D setCreateMetaData(final T source,
			Class<? extends MasterSyncBaseEntity> destinationClass) {

		String contextUser = SessionContext.getSessionContext().getUserContext().getUserId();

		D entity = (D) MapperUtils.map(source, destinationClass);

		setCreatedDateTime(contextUser, entity);
		return entity;
	}

	public static <T, D extends MasterSyncBaseEntity> List<D> setCreateMetaData(final Collection<T> dtoList,
			Class<? extends MasterSyncBaseEntity> entityClass) {

		String contextUser = SessionContext.getSessionContext().getUserContext().getUserId();

		List<D> entities = new ArrayList<>();

		if (null!=dtoList && !dtoList.isEmpty()) {
			dtoList.forEach(dto -> {
				D entity = (D) MapperUtils.map(dto, entityClass);
				setCreatedDateTime(contextUser, entity);
				entities.add(entity);
			});
		}

		return entities;

	}

	private static <D extends MasterSyncBaseEntity> void setCreatedDateTime(String contextUser, D entity) {
		entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setCreatedBy(contextUser);
	}

	private static <D extends MasterSyncBaseEntity> void setUpdatedDateTime(String contextUser, D entity) {
		entity.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setUpdatedBy(contextUser);
	}

}

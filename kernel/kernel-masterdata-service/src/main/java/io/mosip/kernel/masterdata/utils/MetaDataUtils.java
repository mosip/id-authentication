package io.mosip.kernel.masterdata.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.entity.BaseEntity;

/**
 * MetaDataUtils class provide methods to copy values from DTO to entity along
 * with that it create some meta data which is required before an entity to be
 * saved into database.
 * 
 * @author Bal Vikash Sharma
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

	private static String contextUser = "TestAdmin";

	/**
	 * This method takes <code>source</code> object like an DTO and an object which
	 * must extends {@link BaseEntity} and map all values from DTO object to the
	 * <code>destination</code> object and return it.
	 * 
	 * @param <S>
	 *            is a type parameter
	 * @param <D>
	 *            is a type parameter
	 * @param source
	 *            is the source
	 * @param destination
	 *            is the destination
	 * @param mapNullvalues
	 *            if marked as false then field inside source which are null will
	 *            not be mapped into destination
	 * @return <code>destination</code> object which extends {@link BaseEntity}
	 * @throws DataAccessLayerException
	 *             if any error occurs while mapping values
	 * @see MapperUtils#map(Object, Object, Boolean)
	 */
	public static <S, D extends BaseEntity> D setUpdateMetaData(final S source, D destination, Boolean mapNullvalues) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		if (!EmptyCheckUtils.isNullEmpty(authN)) {
			contextUser = authN.getName();
		}

		MapperUtils.map(source, destination, mapNullvalues);

		setUpdatedDateTime(contextUser, destination);
		return destination;
	}

	/**
	 * This method is used to set meta data used for delete.
	 * 
	 * @param entity
	 *            which extends base entity
	 * @return entity having isDeleted value as true and deleted times
	 */
	public static <E extends BaseEntity> E setDeleteMetaData(final E entity) {
		entity.setUpdatedBy(contextUser);
		entity.setIsDeleted(true);
		entity.setDeletedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		return entity;
	}

	/**
	 * This method takes <code>source</code> object like an DTO and a class which
	 * must extends {@link BaseEntity} and map all values from DTO object to the
	 * <code>destinationClass</code> object and return it.
	 * 
	 * @param <T>
	 *            is a type parameter
	 * @param <D>
	 *            is a type parameter
	 * @param source
	 *            is the source
	 * @param destinationClass
	 *            is the destination class
	 * @return an entity class which extends {@link BaseEntity}
	 * @throws DataAccessLayerException
	 *             if any error occurs while mapping values
	 * @see MapperUtils#map(Object, Class)
	 */
	public static <T, D extends BaseEntity> D setCreateMetaData(final T source,
			Class<? extends BaseEntity> destinationClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		if (!EmptyCheckUtils.isNullEmpty(authN)) {
			contextUser = authN.getName();
		}

		D entity = (D) MapperUtils.map(source, destinationClass);

		setCreatedDateTime(contextUser, entity);
		return entity;
	}

	public static <T, D extends BaseEntity> List<D> setCreateMetaData(final Collection<T> dtoList,
			Class<? extends BaseEntity> entityClass) {
		Objects.requireNonNull(dtoList, "dtoList should not be null");
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();

		if (!EmptyCheckUtils.isNullEmpty(authN)) {
			contextUser = authN.getName();
		}
		List<D> entities = new ArrayList<>();

		dtoList.forEach(dto -> {
			D entity = (D) MapperUtils.map(dto, entityClass);
			setCreatedDateTime(contextUser, entity);
			entities.add(entity);
		});

		return entities;

	}

	private static <D extends BaseEntity> void setCreatedDateTime(String contextUser, D entity) {
		entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setCreatedBy(contextUser);
	}

	private static <D extends BaseEntity> void setUpdatedDateTime(String contextUser, D entity) {
		entity.setUpdatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setUpdatedBy(contextUser);
	}
	
	public static String getContextUser() {
		return contextUser;
	}
	
	public static LocalDateTime getCurrentDateTime() {
		return LocalDateTime.now(ZoneId.of("UTC"));
	}

}

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

	/**
	 * This method takes <code>source</code> object like an DTO and a class which
	 * must extends {@link BaseEntity} and map all values from DTO object to the
	 * <code>destinationClass</code> object and return it.
	 * 
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
		String contextUser = "TestAdmin";
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
		String contextUser = authN.getName();
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

}

package io.mosip.kernel.masterdata.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	MapperUtils mapperUtils;

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
	 */
	public <T, D extends BaseEntity> D setCreateMetaData(final T source, Class<? extends BaseEntity> destinationClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();

		D entity = (D) mapperUtils.map(source, destinationClass);

		setCreatedDateTime(contextUser, entity);
		return entity;
	}

	public <T, D extends BaseEntity> List<D> setCreateMetaData(final Collection<T> dtoList,
			Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
		List<D> entities = new ArrayList<>();

		dtoList.forEach(dto -> {
			D entity = (D) mapperUtils.map(dto, entityClass);
			setCreatedDateTime(contextUser, entity);
			entities.add(entity);
		});

		return entities;

	}

	private <D extends BaseEntity> void setCreatedDateTime(String contextUser, D entity) {
		entity.setCreatedDateTime(LocalDateTime.now(ZoneId.of("UTC")));
		entity.setCreatedBy(contextUser);
	}

}

package io.mosip.kernel.masterdata.utils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EmbeddedId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.masterdata.entity.BaseEntity;

@Component
public class MetaDataUtils {

	@Autowired
	private DataMapper dataMapper;

	/**
	 * This method map the value of <code>dto</code> to an object of type
	 * <code>entityClass</code>. If entityClass object have any composite primary
	 * key it will map that value too.
	 * 
	 * @param dto
	 *            source object.
	 * 
	 * @param entityClass
	 *            class type of destination object to be created.
	 * 
	 * @return an new Object of <code>entityClass</code>.
	 * 
	 * @throws IllegalArgumentException
	 *             when any error occurred while mapping <code>dto</code> values to
	 *             embedded Id
	 */
	public <T, D extends BaseEntity> D setCreateMetaData(final T dto, Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
		@SuppressWarnings("unchecked")
		D entity = (D) dataMapper.map(dto, entityClass, true, null, null, true);

		// to map embedded id object START
		Field fields[] = entity.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.isAnnotationPresent(EmbeddedId.class)) {
				try {
					Object id = field.getType().newInstance();
					dataMapper.map(dto, id, true, null, null, true);
					field.setAccessible(true);
					field.set(entity, id);
					field.setAccessible(false);
					break;
				} catch (Exception e) {
					throw new io.mosip.kernel.core.exception.IllegalArgumentException("",
							"Error while mapping Embedded Id fields", e);
				}
			}
		}
		// to map embedded id object END

		setMetaData(contextUser, entity);
		return entity;
	}

	public <T, D extends BaseEntity> List<D> setCreateMetaData(final Collection<T> dtoList,
			Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
		List<D> entities = new ArrayList<>();

		dtoList.forEach(dto -> {
			@SuppressWarnings("unchecked")
			D entity = (D) dataMapper.map(dto, entityClass, true, null, null, true);
			setMetaData(contextUser, entity);
			entities.add(entity);

		});
		return entities;

	}

	private <D extends BaseEntity> void setMetaData(String contextUser, D entity) {
		LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC"));
		LocalDateTime utime = LocalDateTime.now(ZoneId.of("UTC"));
		entity.setCreatedBy(contextUser);
		entity.setCreatedtimes(time);
	}

	/*
	 * public <T> List<T> setUpdateMetaData(final Collection<T> entityList) { //
	 * ForEach DTO to Entity // Set updateBY // SEt UpdatedAt }
	 * 
	 * public <T> List<T> setDeleteMetaData(final Collection<T> entityList) { //
	 * ForEach DTO to Entity // Set isDelted // Set delttedAt // SEt UpdatedBy }
	 * 
	 * public <T> List<T> setActiveMetaData(final Collection<T> entityList) { //
	 * ForEach DTO to Entity // Set isActive // Set updateBY // SEt UpdatedAt }
	 */
}

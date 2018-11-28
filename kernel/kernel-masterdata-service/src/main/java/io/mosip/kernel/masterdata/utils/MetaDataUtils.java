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

import io.mosip.kernel.core.datamapper.spi.DataMapper;
import io.mosip.kernel.masterdata.entity.BaseEntity;

@Component
public class MetaDataUtils {

	@Autowired
	private DataMapper dataMapper;

	public <T, D extends BaseEntity> D setCreateMetaData(final T dto, Class<? extends BaseEntity> entityClass) {
		Authentication authN = SecurityContextHolder.getContext().getAuthentication();
		String contextUser = authN.getName();
		@SuppressWarnings("unchecked")
		D entity = (D) dataMapper.map(dto, entityClass, true, null, null, true);
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

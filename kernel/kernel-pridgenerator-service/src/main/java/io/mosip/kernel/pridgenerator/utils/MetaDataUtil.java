package io.mosip.kernel.pridgenerator.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import io.mosip.kernel.pridgenerator.constant.PRIDGeneratorConstant;
import io.mosip.kernel.pridgenerator.entity.BaseEntity;

/**
 * Utility class for prid Generator
 * 
 * @author Urvil Joshi
 * @since 1.0.0
 *
 */
@Component
public class MetaDataUtil {

	/**
	 * Function to set metadata
	 * 
	 * @param entity entity
	 * @return <T> Entity with metadata
	 */
	public <T extends BaseEntity> T setCreateMetaData(T entity) {
		String contextUser = PRIDGeneratorConstant.DEFAULTADMIN_MOSIP_IO;
		LocalDateTime time = LocalDateTime.now(ZoneId.of(PRIDGeneratorConstant.UTC));
		entity.setCreatedBy(contextUser);
		entity.setCreatedtimes(time);
		entity.setIsDeleted(false);
		return entity;
	}

	/**
	 * Function to set metadata for update operation
	 * 
	 * @param entity entity
	 * @return <T> Entity with metadata
	 */
	public <T extends BaseEntity> T setUpdateMetaData(T entity) {
		String contextUser = PRIDGeneratorConstant.DEFAULTADMIN_MOSIP_IO;
		LocalDateTime time = LocalDateTime.now(ZoneId.of(PRIDGeneratorConstant.UTC));
		entity.setUpdatedBy(contextUser);
		entity.setUpdatedtimes(time);
		entity.setIsDeleted(false);
		return entity;
	}

}

package io.mosip.kernel.masterdata.config;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.persistence.Embeddable;
import javax.persistence.Id;
import javax.persistence.IdClass;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.mosip.kernel.masterdata.validator.ValidLangCode;

/**
 * @author Bal Vikash Sharma
 *
 */
@Component
public class MasterDataInterceptor extends EmptyInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MasterDataInterceptor.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -5768623208162589496L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onDelete(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

		LOGGER.info("Delete call for : {}", entity.getClass().getSimpleName());
		super.onDelete(entity, id, state, propertyNames, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onFlushDirty(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.Object[],
	 * java.lang.String[], org.hibernate.type.Type[])
	 */
	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		LOGGER.info("Update call for : {}", entity.getClass().getSimpleName());
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onLoad(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

		LOGGER.info("Get call for : {}", entity.getClass().getSimpleName());
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {

		Field[] fields = entity.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.getType().isAnnotationPresent(Embeddable.class)) {
				try {
					field.setAccessible(true);
					Object value = field.get(entity);
					Field[] idVar = value.getClass().getDeclaredFields();
					for (Field f : idVar) {
						setValueToUpperCase(value, f);
					}
					field.setAccessible(false);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					LOGGER.error(e.getMessage());
				}

			} else if (field.getType().isAnnotationPresent(Id.class)) {

			} else if (field.getType().isAnnotationPresent(IdClass.class)) {

			} else if (field.getType().isAnnotationPresent(ValidLangCode.class)) {
				field.setAccessible(true);
				try {
					Object value = field.get(entity);
					setValueToUpperCase(value, field);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					LOGGER.error(e.getMessage());
				}

				field.setAccessible(false);
			}
		}
		LOGGER.info("Create call for : {}", entity.getClass().getSimpleName());
		return super.onSave(entity, id, state, propertyNames, types);

	}

	private void setValueToUpperCase(Object value, Field f) throws IllegalAccessException {
		if (f.getType().isAssignableFrom(String.class)) {
			f.setAccessible(true);
			String str = (String) f.get(value);
			f.set(value, str.toUpperCase());
			f.setAccessible(false);
		}
	}

}

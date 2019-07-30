package io.mosip.kernel.dataaccess.hibernate.config;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;



public class EncryptionInterceptor extends EmptyInterceptor {


	private List<String> reqParams= new ArrayList<>();


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		return doSaveOrFlushAction(entity, state, propertyNames, types);
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
		System.out.println("Decrypt the data here ");
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	private boolean doSaveOrFlushAction(Object entity, Object[] state, String[] propertyNames, Type[] types) {
		try {
				Field[] fields = entity.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Encrypted.class)) {
						System.out.println("field name  " + field.getName());
						reqParams.add(field.getName());

					}
				}
				for (int i = 0; i < propertyNames.length; i++) {
					if (reqParams.contains(propertyNames[i])) {
						System.out.println("Value "+state[i]);
					}
				}
				return true;

		} catch (Exception e) {
			// log error
		}
		return false;
	}
}

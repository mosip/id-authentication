package io.mosip.kernel.dataaccess.hibernate.config;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import io.mosip.kernel.dataaccess.hibernate.entity.SecreteKeyStore;



public class EncryptionInterceptor extends EmptyInterceptor {


	private List<String> reqParams;

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
		reqParams= new ArrayList<>();
		try {
			if (entity instanceof SecreteKeyStore) {
				return true;
			} else {
				Field[] fields = entity.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Encrypted.class)) {
						System.out.println("Value " + field.getName());
						reqParams.add(field.getName());

					}
				}
				for (int i = 0; i < propertyNames.length; i++) {
					if (reqParams.contains(propertyNames[i])) {
						String plainText = (String) state[i];
						if (plainText != null && !plainText.trim().isEmpty()) {
							state[i] = SimpleAES.decrypt(plainText);
							System.out.println("Encry " + state[i]);
						}
					}
				}
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	private boolean doSaveOrFlushAction(Object entity, Object[] state, String[] propertyNames, Type[] types) {
		try {
			reqParams= new ArrayList<>();
			if (entity instanceof SecreteKeyStore) {
				return true;
			} else {
				Field[] fields = entity.getClass().getDeclaredFields();
				for (Field field : fields) {
					if (field.isAnnotationPresent(Encrypted.class)) {
						System.out.println("Value " + field.getName());
						reqParams.add(field.getName());

					}
				}
				for (int i = 0; i < propertyNames.length; i++) {
					if (reqParams.contains(propertyNames[i])) {
						String plainText = (String) state[i];
						if (plainText != null && !plainText.trim().isEmpty()) {
							state[i] = SimpleAES.encrypt(plainText);
							System.out.println("Encry " + state[i]);
						}
					}
				}
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

}

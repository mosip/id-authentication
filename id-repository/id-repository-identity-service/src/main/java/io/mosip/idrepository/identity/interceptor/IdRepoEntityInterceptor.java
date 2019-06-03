package io.mosip.idrepository.identity.interceptor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.identity.entity.Uin;
import io.mosip.idrepository.identity.entity.UinHistory;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * The Class IdRepoEntityInterceptor.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoEntityInterceptor extends EmptyInterceptor {

	private static final String ID_REPO_ENTITY_INTERCEPTOR = "IdRepoEntityInterceptor";

	private static final String UIN_DATA_HASH = "uinDataHash";

	private static final String UIN_DATA = "uinData";

	/** The mosip logger. */
	private transient Logger mosipLogger = IdRepoLogger.getLogger(IdRepoEntityInterceptor.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4985336846122302850L;

	@Autowired
	private transient IdRepoSecurityManager securityManager;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.EmptyInterceptor#onSave(java.lang.Object,
	 * java.io.Serializable, java.lang.Object[], java.lang.String[],
	 * org.hibernate.type.Type[])
	 */
	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		try {
			if (entity instanceof Uin) {
				Uin uinEntity = (Uin) entity;
				byte[] encryptedData = securityManager.encrypt(uinEntity.getUinData());
				uinEntity.setUinData(encryptedData);

				List<String> uinList = Arrays.asList(uinEntity.getUin().split("_"));
				byte[] encryptedUinByteWithSalt = securityManager.encryptWithSalt(uinList.get(1).getBytes(),
						CryptoUtil.decodeBase64(uinList.get(2)));
				String encryptedUinWithSalt = uinList.get(0) + "_" + new String(encryptedUinByteWithSalt);
				uinEntity.setUin(encryptedUinWithSalt);
				
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf(UIN_DATA);
				state[indexOfData] = encryptedData;
				int indexOfUin = propertyNamesList.indexOf("uin");
				state[indexOfUin] = encryptedUinWithSalt;
				return super.onSave(uinEntity, id, state, propertyNames, types);
			}
			if (entity instanceof UinHistory) {
				UinHistory uinHEntity = (UinHistory) entity;
				uinHEntity.setUinData(securityManager.encrypt(uinHEntity.getUinData()));
				
				List<String> uinList = Arrays.asList(uinHEntity.getUin().split("_"));
				byte[] encryptedUinByteWithSalt = securityManager.encryptWithSalt(uinList.get(1).getBytes(),
						CryptoUtil.decodeBase64(uinList.get(2)));
				String encryptedUinWithSalt = uinList.get(0) + "_" + new String(encryptedUinByteWithSalt);
				uinHEntity.setUin(encryptedUinWithSalt);
				
				return super.onSave(uinHEntity, id, state, propertyNames, types);
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_ENTITY_INTERCEPTOR, "onSave", "\n" + e.getMessage());
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onSave(entity, id, state, propertyNames, types);
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
		try {
			if (entity instanceof Uin || entity instanceof UinHistory) {
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf(UIN_DATA);
				state[indexOfData] = securityManager.decrypt((byte[]) state[indexOfData]);

				if (!StringUtils.equals(securityManager.hash((byte[]) state[indexOfData]),
						(String) state[propertyNamesList.indexOf(UIN_DATA_HASH)])) {
					throw new IdRepoAppUncheckedException(IdRepoErrorConstants.IDENTITY_HASH_MISMATCH);
				}
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_ENTITY_INTERCEPTOR, "onLoad", "\n" + e.getMessage());
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		try {
			if (entity instanceof Uin) {
				Uin uinEntity = (Uin) entity;
				byte[] encryptedData = securityManager.encrypt(uinEntity.getUinData());
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int indexOfData = propertyNamesList.indexOf(UIN_DATA);
				currentState[indexOfData] = encryptedData;
				return super.onFlushDirty(uinEntity, id, currentState, previousState, propertyNames, types);
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_ENTITY_INTERCEPTOR, "onSave", "\n" + e.getMessage());
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}
}

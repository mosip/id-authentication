package io.mosip.idrepository.vid.interceptor;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.IdRepoConstants;
import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * The Class IdRepoEntityInterceptor - Interceptor for repository calls and
 * allows to update/modify the entity data.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoVidEntityInterceptor extends EmptyInterceptor {

	/** The Constant ID_REPO_ENTITY_INTERCEPTOR. */
	private static final String ID_REPO_ENTITY_INTERCEPTOR = "IdRepoEntityInterceptor";

	/** The mosip logger. */
	private transient Logger mosipLogger = IdRepoLogger.getLogger(IdRepoVidEntityInterceptor.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4985336846122302850L;
	
	/** The IdRepo Security Manager */
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
			if (entity instanceof Vid) {
				Vid vidEntity = (Vid) entity;
				List<String> propertyNamesList = Arrays.asList(propertyNames);
				int uinIndex = propertyNamesList.indexOf("uin");
				List<String> uinList = Arrays.asList(vidEntity.getUin().split(IdRepoConstants.SPLITTER.getValue()));
				byte[] encryptedUinByteWithSalt = securityManager.encryptWithSalt(uinList.get(1).getBytes(),
						CryptoUtil.decodeBase64(uinList.get(2)));
				String encryptedUinWithSalt = uinList.get(0) + IdRepoConstants.SPLITTER.getValue() + new String(encryptedUinByteWithSalt);
				vidEntity.setUin(encryptedUinWithSalt);
				state[uinIndex] = vidEntity.getUin();
				entity = vidEntity;
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_ENTITY_INTERCEPTOR, "onSave", "\n" + e.getMessage());
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onSave(entity, id, state, propertyNames, types);
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
		try {
			if (entity instanceof Vid) {

				List<String> propertyNamesList = Arrays.asList(propertyNames);
				int uinIndex = propertyNamesList.indexOf("uin");
				Vid vidEntity = (Vid) entity;
				List<String> uinList = Arrays.asList(vidEntity.getUin().split(IdRepoConstants.SPLITTER.getValue()));
				byte[] encryptedUinByteWithSalt = securityManager.encryptWithSalt(uinList.get(1).getBytes(),
						CryptoUtil.decodeBase64(uinList.get(2)));
				String encryptedUinWithSalt = uinList.get(0) + IdRepoConstants.SPLITTER.getValue() + new String(encryptedUinByteWithSalt);
				vidEntity.setUin(encryptedUinWithSalt);
				currentState[uinIndex] = vidEntity.getUin();
				entity = vidEntity;
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoSecurityManager.getUser(), ID_REPO_ENTITY_INTERCEPTOR, "onFlushDirty", "\n" + e.getMessage());
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onFlushDirty(entity, id, currentState, currentState, propertyNames, types);
	}
}

package io.mosip.idrepository.vid.interceptor;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.assertj.core.util.Arrays;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.idrepository.core.constant.IdRepoErrorConstants;
import io.mosip.idrepository.core.exception.IdRepoAppException;
import io.mosip.idrepository.core.exception.IdRepoAppUncheckedException;
import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.idrepository.vid.entity.Vid;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class IdRepoEntityInterceptor.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoVidEntityInterceptor extends EmptyInterceptor {

	private static final String ID_REPO_ENTITY_INTERCEPTOR = "IdRepoEntityInterceptor";

	/** The mosip logger. */
	private transient Logger mosipLogger = IdRepoLogger.getLogger(IdRepoVidEntityInterceptor.class);

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4985336846122302850L;

	@Autowired
	private IdRepoSecurityManager securityManager;

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
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int uinIndex = propertyNamesList.indexOf("uin");
				vidEntity.setUin(new String(securityManager.encrypt(vidEntity.getUin().getBytes())));
				state[uinIndex] = vidEntity.getUin();
				entity = vidEntity;
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
			if (entity instanceof Vid) {
				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int uinIndex = propertyNamesList.indexOf("uin");
				int uinHashIndex = propertyNamesList.indexOf("uinHash");
				state[uinIndex] = new String(securityManager.decrypt(String.valueOf(state[uinIndex]).getBytes()));
				if (!StringUtils.equals(String.valueOf(state[uinHashIndex]),
						securityManager.hash(String.valueOf(state[uinIndex]).getBytes()))) {
					throw new IdRepoAppUncheckedException(IdRepoErrorConstants.UIN_HASH_MISMATCH);
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
			if (entity instanceof Vid) {

				List<Object> propertyNamesList = Arrays.asList(propertyNames);
				int uinIndex = propertyNamesList.indexOf("uin");
				Vid vidEntity = (Vid) entity;
				vidEntity.setUin(new String(securityManager.encrypt(vidEntity.getUin().getBytes())));
				currentState[uinIndex] = vidEntity.getUin();
				entity = vidEntity;
			}
		} catch (IdRepoAppException e) {
			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_ENTITY_INTERCEPTOR, "onFlushDirty", "\n" + e.getMessage());
			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
		}
		return super.onFlushDirty(entity, id, currentState, currentState, propertyNames, types);
	}
}

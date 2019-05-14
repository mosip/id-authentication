package io.mosip.idrepository.vid.interceptor;

import java.io.IOException;
import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import io.mosip.idrepository.core.logger.IdRepoLogger;
import io.mosip.idrepository.core.security.IdRepoSecurityManager;
import io.mosip.kernel.core.logger.spi.Logger;

/**
 * The Class IdRepoEntityInterceptor.
 *
 * @author Manoj SP
 */
@Component
public class IdRepoEntityInterceptor extends EmptyInterceptor {

	private static final String ID_REPO_ENTITY_INTERCEPTOR = "IdRepoEntityInterceptor";

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
//		try {
//		} catch (IdRepoAppException e) {
//			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_ENTITY_INTERCEPTOR, "onSave", "\n" + e.getMessage());
//			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
//		}
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
//		try {
//		} catch (IdRepoAppException e) {
//			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_ENTITY_INTERCEPTOR, "onLoad", "\n" + e.getMessage());
//			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
//		}
		return super.onLoad(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
//		try {
//		} catch (IdRepoAppException e) {
//			mosipLogger.error(IdRepoLogger.getUin(), ID_REPO_ENTITY_INTERCEPTOR, "onSave", "\n" + e.getMessage());
//			throw new IdRepoAppUncheckedException(IdRepoErrorConstants.ENCRYPTION_DECRYPTION_FAILED, e);
//		}
		return super.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
	}
}

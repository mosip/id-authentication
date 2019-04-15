/**
 * 
 */
package io.mosip.kernel.auth.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.auth.config.MosipEnvironment;
import io.mosip.kernel.auth.constant.AuthConstant;

/**
 * @author Ramadurai Pandian
 *
 */
@Component
public class UserStoreFactoryImpl implements UserStoreFactory {

	@Autowired
	private MosipEnvironment mosipEnvironment;

	private Map<String, IDataStore> dataStoreMap = null;

	UserStoreFactoryImpl() {

	}

	@PostConstruct
	private void init() {
		buildDataStoreMap();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.auth.factory.UserStoreFactory#getUserStores()
	 */
	@Override
	public Map<String, IDataStore> getUserStores() {
		return dataStoreMap;
	}

	public void buildDataStoreMap() {
		dataStoreMap = new HashMap<>();
		String datasources = mosipEnvironment.getDataStores();
		List<String> dataStoreList = Arrays.asList(datasources.split("\\s*,\\s*"));

		for (String ds : dataStoreList) {
			if (dataStoreMap.get(ds) == null) {
				DataBaseConfig dataBaseConfig = new DataBaseConfig();
				dataBaseConfig.setUrl(mosipEnvironment.getUrl(ds));
				dataBaseConfig.setPort(mosipEnvironment.getPort(ds));
				dataBaseConfig.setUsername(mosipEnvironment.getUserName(ds));
				dataBaseConfig.setPassword(mosipEnvironment.getPassword(ds));
				dataBaseConfig.setDriverName(mosipEnvironment.getDriverName(ds));
				dataBaseConfig.setSchemas(ds);
				if (ds.contains(AuthConstant.LDAP)) {
					IDataStore idatastore = new ILdapDataStore(dataBaseConfig);
					dataStoreMap.put(ds, idatastore);
				} else {
					IDataStore idatastore = new DBDataStore(dataBaseConfig);
					dataStoreMap.put(ds, idatastore);
				}

			}

		}
	}

	@Override
	public IDataStore getDataStoreBasedOnApp(String appId) {
		String datasource = null;
		if (appId != null) {
			datasource = mosipEnvironment.getDataStore(appId.toLowerCase() + AuthConstant.DATASOURCE);
		}
		return dataStoreMap.get(datasource);
	}

}

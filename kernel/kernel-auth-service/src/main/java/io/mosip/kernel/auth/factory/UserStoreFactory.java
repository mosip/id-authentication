/**
 * 
 */
package io.mosip.kernel.auth.factory;

import java.util.Map;

/**
 * @author Ramadurai Pandian
 *
 */
public interface UserStoreFactory {

	Map<String, IDataStore> getUserStores();

	IDataStore getDataStoreBasedOnApp(String appId);
}

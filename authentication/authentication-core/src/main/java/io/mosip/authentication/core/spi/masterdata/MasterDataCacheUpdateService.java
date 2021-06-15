package io.mosip.authentication.core.spi.masterdata;

import io.mosip.kernel.core.websub.model.EventModel;

/**
 * The Interface MasterDataCacheUpdateService.
 * 
 * @author Loganathan Sekar
 */
public interface MasterDataCacheUpdateService {

	/**
	 * Update templates.
	 *
	 * @param model the model
	 */
	void updateTemplates(EventModel model);

	/**
	 * Update titles.
	 *
	 * @param model the model
	 */
	void updateTitles(EventModel model);

}
package io.mosip.authentication.core.spi.masterdata;

import io.mosip.kernel.core.websub.model.EventModel;

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
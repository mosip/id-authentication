package io.mosip.util;

import java.io.InputStream;

/**
 * The Interface UinCardGenerator.
 * 
 * @author M1048358 Alok
 *
 * @param <I> the generic type
 */
public interface UinCardGenerator<I> {

	/**
	 * Generate uin card.
	 *
	 * @param in the in
	 * @param type the type
	 * @return the i
	 */
	public I generateUinCard(InputStream in, UinCardType type);
}

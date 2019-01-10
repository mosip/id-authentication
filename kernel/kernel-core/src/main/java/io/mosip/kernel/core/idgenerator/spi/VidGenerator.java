package io.mosip.kernel.core.idgenerator.spi;

/**
 * This is an interface for the generation of RID
 * 
 * @author Sidhant Agarwal
 * @author Megha Tanga
 * @since 1.0.0
 *
 */
public interface VidGenerator<T> {

	T generateId();

}

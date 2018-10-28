package io.mosip.kernel.core.idgenerator.spi;

/**
 * This is an interface for the generation of RID
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface MosipVidGenerator<T> {
	/**
	 * Function to generate an Id
	 * 
	 * @return The generated id
	 */
	T generateId(String uin);

}

/**
 * 
 * 
 */
package io.mosip.kernel.core.virusscanner.spi;

/**
 * @author Mukul Puspam
 *
 * @param <U>
 *            Return type
 * 
 * @param <V>
 *            file path
 */

public interface VirusScanner<U, V> {

	/**
	 * Scan file.
	 *
	 * @param fileName
	 *            the file name
	 * @return the u
	 */
	U scanFile(V fileName);

	/**
	 * Scan folder.
	 *
	 * @param folderPath
	 *            the folder path
	 * @return the u
	 */
	U scanFolder(V folderPath);
}

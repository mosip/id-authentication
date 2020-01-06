package io.mosip.registration.device.scanner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @author balamurugan ramamoorthy
 * @since 1.0.0
 */
public interface IMosipDocumentScannerService {

	/**
	 * This method is used to check whether the scanner is connected to the machine
	 * 
	 * @return boolean - the value is true if the scanner is connected
	 */
	boolean isConnected();

	/**
	 * This is used to connect the scanner device and get the scanned document
	 * 
	 * @return byte[] - The scanned document data
	 */
	BufferedImage scan();

	/**
	 * This method converts the separate scanned file into single document
	 * 
	 * @param bufferedImages
	 *            - the scanned files
	 * @return byte[] - The single document which contains all the scanned files
	 */
	byte[] asPDF(List<BufferedImage> bufferedImages);

	/**
	 * This method is used to merge all the scanned images into single image side by
	 * side
	 * 
	 * @param bufferedImages
	 *            - holds all the separate scanned pages
	 * @return byte[] - The final single file which contains all the scanned files
	 * @throws IOException
	 *             - holds the ioexception
	 */
	byte[] asImage(List<BufferedImage> bufferedImages) throws IOException;

	/**
	 * This method converts the BufferedImage to byte[]
	 * 
	 * @param bufferedImage
	 *            - holds the scanned image from the scanner
	 * @return byte[] - scanned document Content
	 * @throws IOException
	 *             - holds the ioexcepion
	 */
	byte[] getImageBytesFromBufferedImage(BufferedImage bufferedImage) throws IOException;

	/**
	 * This method is used to convert the pdf file into images
	 * 
	 * @param pdfBytes
	 *            - pdf file in bytes
	 * @return List - holds the list of buffered images
	 * @throws IOException
	 *             - holds the ioexception
	 */
	List<BufferedImage> pdfToImages(byte[] pdfBytes) throws IOException;

}
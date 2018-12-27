package io.mosip.registration.device.scanner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public interface DocumentScannerService {

	/**
	 * This method is used to check whether the scanner is connected to the machine
	 * 
	 * @return boolean - the value is true if the scanner is connected
	 */
	boolean isScannerConnected();

	/**
	 * This is used to connect the scanner device and get the scanned document
	 * 
	 * @return byte[] - The scanned document data
	 */
	byte[] scanDocument();

	/**
	 * This method converts the separate scanned file into single document
	 * 
	 * @param bufferedImages
	 *            - the scanned files
	 * @return byte[] - The single document which contains all the scanned files
	 */
	byte[] getSinglePDFInBytes(List<BufferedImage> bufferedImages);

	/**
	 * This method is used to merge all the scanned images into single image side by
	 * side
	 * 
	 * @param bufferedImages
	 *            - holds all the separate scanned pages
	 * @return byte[] - The final single file which contains all the scanned files
	 * @throws IOException
	 */
	byte[] getSingleImageFromList(List<BufferedImage> bufferedImages) throws IOException;

}
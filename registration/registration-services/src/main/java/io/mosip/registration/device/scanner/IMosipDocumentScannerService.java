package io.mosip.registration.device.scanner;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

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
	 */
	byte[] asImage(List<BufferedImage> bufferedImages) throws IOException;

	/**
	 * This method is to get the image file in byte array from the given
	 * BufferedImage
	 * 
	 * @param bufferedImage
	 * @return byte[]
	 * @throws IOException
	 */
	byte[] getImageBytesFromBufferedImage(BufferedImage bufferedImage) throws IOException;

	/**
	 * This method is used to convert the pdf file into images
	 * 
	 * @param pdfBytes
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	List<BufferedImage> pdfToImages(byte[] pdfBytes) throws IOException;

}
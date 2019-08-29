package io.mosip.kernel.core.pdfgenerator.spi;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

/**
 * This interface is has specifications for PDF generation from different types.
 * 
 * The user of this interface will have basic functionalities related to PDF.
 * 
 * @author Urvil Joshi
 * @author Uday Kumar
 * @author Neha
 * 
 * @since 1.0.0
 */
public interface PDFGenerator {
	/**
	 * Converts HTML obtained from an {@link InputStream} to a PDF written to an
	 * {@link OutputStream}.
	 * 
	 * @param htmlStream the processedTemplate in the form of a {@link InputStream}
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @return outpustStream PDF Output Stream (PDF stream)
	 */
	public OutputStream generate(InputStream htmlStream) throws IOException;

	/**
	 * This method will convert process Template as String to outpustStream
	 * 
	 * @param template the processedTemplate in the form of a {@link String}
	 * @return OutputStream PDF Output Stream (PDF stream)
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public OutputStream generate(String template) throws IOException;

	/**
	 * This method will take input as template file and convert template to PDF and
	 * save to the given path with given fileName.
	 * 
	 * @param templatePath   the processedTemplate in the form of a {@link String}
	 * @param outputFilePath Output File Path
	 * @param outputFileName Output File Name
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void generate(String templatePath, String outputFilePath, String outputFileName) throws IOException;

	/**
	 * This method will convert InputStream to OutputStream.
	 * 
	 * @param dataStream          the processedTemplate in the form of a {@link InputStream}.
	 * @param resourceLoc resourceLoction {@link String}.
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @return outpustStream PDF Output Stream (PDF stream).
	 */
	public OutputStream generate(InputStream dataStream, String resourceLoc) throws IOException;

	/**
	 * This method will convert BufferedImage list to Byte Array.
	 * 
	 * @param bufferedImages the input image to convert as PDF.
	 * @return  array comprising PDF.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] asPDF(List<BufferedImage> bufferedImages) throws IOException;

	/**
	 * This method will merge all the PDF files.
	 * 
	 * @param pdfLists the URL list of PDF files.
	 * @return the byte array comprising merged file.
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] mergePDF(List<URL> pdfLists) throws IOException;

	/**
	 * Converts data obtained from an {@link InputStream} to a password protected
	 * PDF written to an {@link OutputStream}.
	 * 
	 * If password is null or empty, PDF will not be encrypted.
	 * 
	 * @param dataInputStream the processedTemplate in the form of a
	 *                        {@link InputStream}
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @return Password Protected PDF Output Stream (PDF stream)
	 */
	public OutputStream generate(InputStream dataInputStream, byte[] password) throws IOException;

}

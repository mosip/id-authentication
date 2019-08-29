package io.mosip.kernel.core.pdfgenerator.spi;

/**
 * The PdfGenerator can be used mostly when converting processed
 * Template to PDF. It contains a series of methods that accept processed
 * Template as a {@link String}, {@link File}, or {@link InputStream}, and
 * convert it to PDF in the form of an {@link OutputStream}, {@link File}
 * 
 * @author M1046571
 * @author Neha
 * 
 * @since 1.0.0
 *
 */
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
 *
 */
public interface PDFGenerator {
	/**
	 * This method will convert InputStream to OutputStream
	 * 
	 * @param is the processedTemplate in the form of a {@link InputStream}
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @return outpustStream
	 */
	public OutputStream generate(InputStream is) throws IOException;

	/**
	 * This method will convert process Template as String to outpustStream
	 * 
	 * @param template the processedTemplate in the form of a {@link String}
	 * @return OutputStream
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
	 * This method will convert InputStream to OutputStream
	 * 
	 * @param is          the processedTemplate in the form of a {@link InputStream}
	 * @param resourceLoc resourceLoction {@link String}
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @return outpustStream
	 */
	public OutputStream generate(InputStream is, String resourceLoc) throws IOException;

	/**
	 * This method will convert BufferedImage list to Byte Array
	 * 
	 * @param bufferedImages the input image to convert as PDF
	 * @return byte array
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public byte[] asPDF(List<BufferedImage> bufferedImages) throws IOException;

	/**
	 * This method will merge all the PDF files
	 * 
	 * @param pdfLists the URL list of PDF files
	 * @return the byte array comprising merged file
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

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

public interface PDFGenerator {
	/**
	 * This method will convert InputStream to OutputStream
	 * 
	 * @param is
	 *            the processedTemplate in the form of a {@link InputStream}
	 * @throws IOException
	 *             throws IOException
	 * @return outpustStream
	 */
	public OutputStream generate(InputStream is) throws IOException;

	/**
	 * This method will convert process Template as String to outpustStream
	 * 
	 * @param template
	 *            the processedTemplate in the form of a {@link String}
	 * @return OutputStream
	 * @throws IOException
	 *             throws IOException
	 */
	public OutputStream generate(String template) throws IOException;

	/**
	 * This method will take input as template file and convert template to PDF and
	 * save to the given path with given fileName.
	 * 
	 * @param templatePath
	 *            the processedTemplate in the form of a {@link String}
	 * @param outputFilePath
	 *            Output File Path
	 * @param outputFileName
	 *            Output File Name
	 * @throws IOException
	 *             throws IOException
	 */
	public void generate(String templatePath, String outputFilePath, String outputFileName) throws IOException;

	/**
	 * This method will convert InputStream to OutputStream
	 * 
	 * @param is
	 *            the processedTemplate in the form of a {@link InputStream}
	 * @param resourceLoc
	 *            resourceLoction {@link String}
	 * @throws IOException
	 *             throws IOException
	 * @return outpustStream
	 */
	public OutputStream generate(InputStream is, String resourceLoc) throws IOException;

	/**
	 * This method will convert BufferedImage list to Byte Array
	 * 
	 * @param bufferedImages
	 *            the input image to convert as PDF
	 * @return byte array
	 * @throws IOException
	 *             throws IOException
	 */
	public byte[] asPDF(List<BufferedImage> bufferedImages) throws IOException;

	/**
	 * This method will merge all the pdf files
	 * 
	 * @param pdfLists
	 *            the url list of pdf files
	 * @return the byte array of merged file
	 * @throws IOException
	 *             throws IOException
	 */
	public byte[] mergePDF(List<URL> pdfLists) throws IOException;

}

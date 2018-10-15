package io.mosip.kernel.core.spi.pdfgenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface PdfGenerator {
	/**
	 * This method will convert InputStream to OutputStream
	 * 
	 * @param is
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
	 */
	public OutputStream generate(String template) throws IOException;

	/**
	 * This method will take input as template file and convert template to PDF and
	 * save to the given path with given fileName.
	 * 
	 * @param templatePath
	 * @param outputFilePath
	 * @param outputFileName
	 * @throws IOException
	 */
	public void generate(String templatePath, String outputFilePath, String outputFileName) throws IOException;

}

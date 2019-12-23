package io.mosip.kernel.core.pdfgenerator.spi;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.List;

import io.mosip.kernel.core.keymanager.model.CertificateEntry;
import io.mosip.kernel.core.pdfgenerator.model.Rectangle;


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

	/** Signs a PDF and protect it with password
	 * @param pdf byte array of pdf.
	 * @param rectangle {@link Rectangle} class to enclose signing
	 * @param reason reason of signing.
	 * @param pageNumber page number of rectangle.
	 * @param provider {@link Provider}.
	 * @param certificateEntry {@link CertificateEntry} class for certificate and private key as Input;
	 * @param password password for protecting pdf.
	 * @return {@link OutputStream} of signed PDF.
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GeneralSecurityException Signals general security exception while signing.
	 */
	OutputStream signAndEncryptPDF(byte[] pdf,Rectangle rectangle,String reason,int pageNumber,Provider provider,CertificateEntry<X509Certificate, PrivateKey> certificateEntry,String password) throws IOException, GeneralSecurityException;

}

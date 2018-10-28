package io.mosip.kernel.pdfgenerator.itext.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.itextpdf.html2pdf.HtmlConverter;

import io.mosip.kernel.core.pdfgenerator.exception.PdfGeneratorException;
import io.mosip.kernel.core.pdfgenerator.spi.PdfGenerator;
import io.mosip.kernel.pdfgenerator.itext.constant.PdfGeneratorExceptionCodeConstant;

/**
 * The PdfGeneratorImpl is the class you will use most when converting processed
 * Template to PDF. It contains a series of methods that accept processed
 * Template as a {@link String}, {@link File}, or {@link InputStream}, and
 * convert it to PDF in the form of an {@link OutputStream}, {@link File}
 * 
 * @author M1046571
 * @since 1.0.0
 *
 */

@Component
public class PdfGeneratorImpl implements PdfGenerator {
	private static final String OUTPUT_FILE_EXTENSION = ".pdf";
	private static final String FILE_SEPERATOR = System.getProperty("file.separator");

	/**
	 * This method is used to convert Template obtained from an {@link InputStream}
	 * to a PDF file and written to an {@link OutputStream}.
	 *
	 * @param is
	 *            The {@link InputStream} with the source processed Template
	 * @return It will return generated PDF file as {@link OutputStream}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred
	 *
	 */
	@Override
	public OutputStream generate(InputStream is) throws IOException {
		OutputStream os = new ByteArrayOutputStream();
		Objects.requireNonNull(is, "Stream cannot be null");
		try {
			HtmlConverter.convertToPdf(is, os);
		} catch (Exception e) {
			throw new PdfGeneratorException(PdfGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		return os;
	}

	/**
	 * 
	 * Converts a {@link String} containing processed template to an
	 * {@link OutputStream} containing PDF
	 *
	 * @param template
	 *            the processedTemplate in the form of a {@link String}
	 * 
	 * @return It will return generated PDF file as {@link OutputStream}
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred
	 * 
	 * 
	 */

	@Override
	public OutputStream generate(String template) throws IOException {
		OutputStream os = new ByteArrayOutputStream();
		try {
			HtmlConverter.convertToPdf(template, os);
		} catch (Exception e) {
			throw new PdfGeneratorException(PdfGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					PdfGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorMessage(), e);
		}
		return os;
	}

	/**
	 * Converts processed Template stored in a {@link String} to a PDF {@link File}.
	 * 
	 * @param templatePath
	 *            The {@link String} containing the source Processed Template
	 * @param outpuFilePath
	 *            The {@link File} containing the resulting PDF
	 * @param outputFileName
	 *            The {@link String} name of output file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred
	 */
	@Override
	public void generate(String templatePath, String outpuFilePath, String outputFileName) throws IOException {
		File outputFile = new File(outpuFilePath + FILE_SEPERATOR + outputFileName + OUTPUT_FILE_EXTENSION);

		try {
			HtmlConverter.convertToPdf(new File(templatePath), outputFile);

		} catch (Exception e) {
			throw new PdfGeneratorException(PdfGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					PdfGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorMessage(), e);
		}

	}

}

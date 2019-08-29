package io.mosip.kernel.pdfgenerator.itext.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.css.media.MediaDeviceDescription;
import com.itextpdf.html2pdf.css.media.MediaType;
import com.itextpdf.html2pdf.css.util.CssUtils;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import io.mosip.kernel.core.pdfgenerator.exception.PDFGeneratorException;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.util.EmptyCheckUtils;
import io.mosip.kernel.core.util.FileUtils;
import io.mosip.kernel.pdfgenerator.itext.constant.PDFGeneratorExceptionCodeConstant;

/**
 * The PdfGeneratorImpl is the class you will use most when converting processed
 * Template to PDF. It contains a series of methods that accept processed
 * Template as a {@link String}, {@link File}, or {@link InputStream}, and
 * convert it to PDF in the form of an {@link OutputStream}, {@link File}
 * 
 * 
 * @author Uday Kumar
 * @author Neha
 * @author Urvil Joshi
 * 
 * @since 1.0.0
 *
 */
@Component
public class PDFGeneratorImpl implements PDFGenerator {
	private static final String OUTPUT_FILE_EXTENSION = ".pdf";

	@Value("${mosip.kernel.pdf_owner_password}")
	private String pdfOwnerPassword="asd";

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator#generate(java.io.
	 * InputStream)
	 */
	@Override
	public OutputStream generate(InputStream is) throws IOException {
		OutputStream os = new ByteArrayOutputStream();
		Objects.requireNonNull(is, "Stream cannot be null");
		try {
			HtmlConverter.convertToPdf(is, os);
		} catch (Exception e) {
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		return os;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator#generate(java.lang.String)
	 */
	@Override
	public OutputStream generate(String template) throws IOException {
		OutputStream os = new ByteArrayOutputStream();
		try {
			HtmlConverter.convertToPdf(template, os);
		} catch (Exception e) {
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorMessage(), e);
		}
		return os;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator#generate(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void generate(String templatePath, String outpuFilePath, String outputFileName) throws IOException {
		File outputFile = new File(outpuFilePath + outputFileName + OUTPUT_FILE_EXTENSION);

		try {
			HtmlConverter.convertToPdf(new File(templatePath), outputFile);

		} catch (Exception e) {
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorMessage(), e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator#generate(java.io.
	 * InputStream, java.lang.String)
	 */
	@Override
	public OutputStream generate(InputStream is, String resourceLoc) throws IOException {
		Objects.requireNonNull(is, "Stream cannot be null");
		OutputStream os = new ByteArrayOutputStream();
		PdfWriter pdfWriter = new PdfWriter(os);
		PdfDocument pdfDoc = new PdfDocument(pdfWriter);
		ConverterProperties converterProperties = new ConverterProperties();
		pdfDoc.setTagged();
		PageSize pageSize = PageSize.A4.rotate();
		pdfDoc.setDefaultPageSize(pageSize);
		float screenWidth = CssUtils.parseAbsoluteLength("" + pageSize.getWidth());
		MediaDeviceDescription mediaDescription = new MediaDeviceDescription(MediaType.SCREEN);
		mediaDescription.setWidth(screenWidth);
		DefaultFontProvider dfp = new DefaultFontProvider(true, true, false);
		converterProperties.setMediaDeviceDescription(mediaDescription);
		converterProperties.setFontProvider(dfp);
		converterProperties.setBaseUri(resourceLoc);
		converterProperties.setCreateAcroForm(true);
		try {
			HtmlConverter.convertToPdf(is, pdfDoc, converterProperties);
		} catch (Exception e) {
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		return os;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator#asPDF(java.util.List)
	 */
	@Override
	public byte[] asPDF(List<BufferedImage> bufferedImages) throws IOException {
		byte[] scannedPdfFile = null;

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

			PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
			Document document = new Document(new PdfDocument(pdfWriter));

			for (BufferedImage bufferedImage : bufferedImages) {
				Image image = new Image(ImageDataFactory.create(getImageBytesFromBufferedImage(bufferedImage)));
				image.scaleToFit(600, 750);
				document.add(image);
			}

			document.close();
			pdfWriter.close();
			scannedPdfFile = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
		return scannedPdfFile;
	}

	private byte[] getImageBytesFromBufferedImage(BufferedImage bufferedImage) throws IOException {
		byte[] imageInByte;

		ByteArrayOutputStream imagebyteArray = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, "jpg", imagebyteArray);
		imagebyteArray.flush();
		imageInByte = imagebyteArray.toByteArray();
		imagebyteArray.close();

		return imageInByte;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator#mergePDF(java.util.List)
	 */
	@Override
	public byte[] mergePDF(List<URL> pdfFiles) throws IOException {
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			com.itextpdf.text.Document document = new com.itextpdf.text.Document();
			PdfCopy pdfCopy = new PdfCopy(document, byteArrayOutputStream);
			document.open();
			for (URL file : pdfFiles) {
				PdfReader reader = new PdfReader(file);
				pdfCopy.addDocument(reader);
				pdfCopy.freeReader(reader);
				reader.close();
			}
			document.close();
			return byteArrayOutputStream.toByteArray();
		} catch (IOException | DocumentException e) {
			throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
					e.getMessage());
		}
	}

	@Override
	public OutputStream generate(InputStream dataInputStream, byte[] password) throws IOException {
		isValidInputStream(dataInputStream);
		if (password == null || password.length == 0) {
			return generate(dataInputStream);
		} else {
			if (EmptyCheckUtils.isNullEmpty(pdfOwnerPassword)) {
				throw new PDFGeneratorException(
						PDFGeneratorExceptionCodeConstant.OWNER_PASSWORD_NULL_EMPTY_EXCEPTION.getErrorCode(),
						PDFGeneratorExceptionCodeConstant.OWNER_PASSWORD_NULL_EMPTY_EXCEPTION.getErrorMessage());
			}
			OutputStream pdfStream = new ByteArrayOutputStream();
			com.itextpdf.text.Document document = new com.itextpdf.text.Document();
			try {
				com.itextpdf.text.pdf.PdfWriter pdfWriter = com.itextpdf.text.pdf.PdfWriter.getInstance(document,
						pdfStream);
				pdfWriter.setEncryption(password, pdfOwnerPassword.getBytes(),
						com.itextpdf.text.pdf.PdfWriter.ALLOW_PRINTING,
						com.itextpdf.text.pdf.PdfWriter.ENCRYPTION_AES_256);
				document.open();
				XMLWorkerHelper.getInstance().parseXHtml(pdfWriter, document, dataInputStream);
			} catch (DocumentException e) {
				throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
						e.getMessage(), e);
			} catch (IOException e) {
				throw new PDFGeneratorException(PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorCode(),
						PDFGeneratorExceptionCodeConstant.PDF_EXCEPTION.getErrorMessage(), e);
			} finally {
				document.close();
			}
			return pdfStream;
		}
	}

	private void isValidInputStream(InputStream dataInputStream) {
		if (EmptyCheckUtils.isNullEmpty(dataInputStream)) {
			throw new PDFGeneratorException(
					PDFGeneratorExceptionCodeConstant.INPUTSTREAM_NULL_EMPTY_EXCEPTION.getErrorCode(),
					PDFGeneratorExceptionCodeConstant.INPUTSTREAM_NULL_EMPTY_EXCEPTION.getErrorMessage());
		}
	}

	public static void main(String[] args)
			throws IOException, io.mosip.kernel.core.exception.IOException, DocumentException {
		PDFGeneratorImpl com = new PDFGeneratorImpl();
		StringBuilder htmlString = new StringBuilder();
		htmlString.append("<html><body> This is HMTL to PDF conversion Example<table border='2' align='center'> ");
		InputStream is = new ByteArrayInputStream(htmlString.toString().getBytes());
		ByteArrayOutputStream outputStream = (ByteArrayOutputStream) com.generate(is, "use".getBytes());
		File file = new File("C:\\Users\\m1044287\\Downloads\\hsm\\yobro.pdf");
		FileUtils.writeByteArrayToFile(file, outputStream.toByteArray());
	}
}

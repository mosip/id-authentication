package io.mosip.registration.device.scanner.impl;

import static io.mosip.registration.constants.LoggerConstants.LOG_REG_DOC_SCAN_CONTROLLER;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.mosip.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import au.com.southsky.jfreesane.SaneDevice;
import au.com.southsky.jfreesane.SaneException;
import au.com.southsky.jfreesane.SaneSession;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.device.scanner.DocumentScannerService;

/**
 * This class is used to handle all the requests related to scanner devices
 * through Sane Daemon service
 * 
 * @author balamurugan.ramamoorthy
 *
 */
@Service
public class DocumentScannerServiceImpl implements DocumentScannerService {

	@Value("${DOCUMENT_SCANNER_DPI}")
	private int scannerDpi;

	@Value("${DOCUMENT_SCANNER_CONTRAST}")
	private int scannerContrast;

	@Value("${DOCUMENT_SCANNER_BRIGHTNESS}")
	private int scannerBrightness;

	@Value("${DOCUMENT_SCANNER_DEPTH}")
	private int scannerDepth;

	@Value("${DOCUMENT_SCANNER_HOST}")
	private String scannerhost;

	@Value("${DOCUMENT_SCANNER_PORT}")
	private int scannerPort;

	@Value("${DOCUMENT_SCANNER_TIMEOUT}")
	private long scannerTimeout;

	@Value("${DOCUMENT_SCANNER_IMGTYPE}")
	private String scannerImgType;

	private static final Logger LOGGER = AppConfig.getLogger(DocumentScannerServiceImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.device.scanner.impl.DocumentScannerService#
	 * isScannerConnected()
	 */
	@Override
	public boolean isConnected() {

		return isListNotEmpty(getScannerDevices());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.device.scanner.impl.DocumentScannerService#scanDocument
	 * ()
	 */
	@Override
	public BufferedImage scan() {

		BufferedImage bufferedImage = null;
		List<SaneDevice> saneDevices = getScannerDevices();
		if (isListNotEmpty(saneDevices)) {
			SaneDevice saneDevice = saneDevices.get(0);
			try {
				saneDevice.open();

				setScannerSettings(saneDevice);
				bufferedImage = saneDevice.acquireImage();

				saneDevice.close();
			} catch (IOException | SaneException e) {
				LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, e.getMessage());
			}
		}
		return bufferedImage;
	}

	/**
	 * This method is used to set the scanner settings for the given scanner device
	 * 
	 * @param saneDevice
	 *            - the scanner device
	 * @throws IOException
	 * @throws SaneException
	 */
	private void setScannerSettings(SaneDevice saneDevice) throws IOException, SaneException {
		/* setting the resolution in dpi for the quality of the document */
		saneDevice.getOption("resolution").setIntegerValue(scannerDpi);

		saneDevice.getOption("brightness").setIntegerValue(scannerBrightness);

		saneDevice.getOption("contrast").setIntegerValue(scannerContrast);

		saneDevice.getOption("depth").setIntegerValue(scannerDepth);

	}

	/**
	 * This method converts the BufferedImage to byte[]
	 * 
	 * @param bufferedImage
	 *            - holds the scanned image from the scanner
	 * @return byte[] - scanned document Content
	 * @throws IOException
	 */
	public byte[] getImageBytesFromBufferedImage(BufferedImage bufferedImage) throws IOException {
		byte[] imageInByte;

		ByteArrayOutputStream imagebyteArray = new ByteArrayOutputStream();
		ImageIO.write(bufferedImage, scannerImgType, imagebyteArray);
		imagebyteArray.flush();
		imageInByte = imagebyteArray.toByteArray();
		imagebyteArray.close();

		return imageInByte;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.device.scanner.impl.DocumentScannerService#
	 * getSinglePDFInBytes(java.util.List)
	 */
	@Override
	public byte[] asPDF(List<BufferedImage> bufferedImages) {

		byte[] scannedPdfFile = null;
		Document document = new Document();
		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();

			PdfContentByte pdfPage = new PdfContentByte(writer);

			for (BufferedImage bufferedImage : bufferedImages) {
				Image image = Image.getInstance(pdfPage, bufferedImage, 1);
				document.add(image);
			}

			document.close();
			writer.close();
			scannedPdfFile = byteArrayOutputStream.toByteArray();
			byteArrayOutputStream.close();
		} catch (DocumentException | IOException e) {
			LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, e.getMessage());
		}
		return scannedPdfFile;

	}

	/**
	 * This method is used to get the lists of scanners connected to the machine
	 * 
	 * @return List<SaneDevice> - The list of connected scanner devices
	 */
	private List<SaneDevice> getScannerDevices() {
		List<SaneDevice> saneDevices = null;
		try {
			SaneSession session = SaneSession.withRemoteSane(InetAddress.getByName(scannerhost), scannerTimeout,
					TimeUnit.MILLISECONDS);
			saneDevices = session.listDevices();
		} catch (IOException | SaneException e) {
			LOGGER.error(LOG_REG_DOC_SCAN_CONTROLLER, APPLICATION_NAME, APPLICATION_ID, e.getMessage());
		}
		return saneDevices;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.device.scanner.impl.DocumentScannerService#
	 * getSingleImageFromList(java.util.List)
	 */
	@Override
	public byte[] asImage(List<BufferedImage> bufferedImages) throws IOException {
		byte[] newSingleImage = null;
		if (isListNotEmpty(bufferedImages)) {

			if (bufferedImages.size() == 1) {
				return getImageBytesFromBufferedImage(bufferedImages.get(0));
			}
			int offset = 2;
			int width = offset;
			for (BufferedImage bufferedImage : bufferedImages) {
				width += bufferedImage.getWidth();
			}
			int height = Math.max(bufferedImages.get(0).getHeight(), bufferedImages.get(1).getHeight()) + offset;
			BufferedImage singleBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = singleBufferedImage.createGraphics();
			Color oldColor = g2.getColor();
			g2.setPaint(Color.BLACK);
			g2.fillRect(0, 0, width, height);
			g2.setColor(oldColor);
			for (int i = 0; i < bufferedImages.size(); i++) {
				g2.drawImage(bufferedImages.get(i), null, (i * bufferedImages.get(i).getWidth()) + offset, 0);
			}

			g2.dispose();

			newSingleImage = getImageBytesFromBufferedImage(singleBufferedImage);

		}

		return newSingleImage;

	}

	@Override
	public List<BufferedImage> pdfToImages(byte[] pdfBytes) throws IOException {

		List<BufferedImage> bufferedImages = new ArrayList<>();
		PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfBytes));
		@SuppressWarnings("unchecked")
		List<PDPage> list = document.getDocumentCatalog().getAllPages();

		for (PDPage page : list) {
			BufferedImage image = page.convertToImage();
			bufferedImages.add(image);
		}
		document.close();
		return bufferedImages;
	}

	private boolean isListNotEmpty(List<?> values) {
		return values != null && !values.isEmpty();
	}
}

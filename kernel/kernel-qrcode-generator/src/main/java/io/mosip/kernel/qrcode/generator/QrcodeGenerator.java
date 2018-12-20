package io.mosip.kernel.qrcode.generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.qrcode.generator.constant.QrVersion;
import io.mosip.kernel.qrcode.generator.constant.QrcodeConstants;
import io.mosip.kernel.qrcode.generator.constant.QrcodeExceptionConstants;
import io.mosip.kernel.qrcode.generator.util.QrcodegeneratorUtils;

/**
 * Class which provides functionality to generate QR Code
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */

public class QrcodeGenerator {

	/**
	 * {@link QRCodeWriter} instance
	 */
	private static QRCodeWriter qrCodeWriter;
	/**
	 * Configurations for QrCode Generator
	 */
	private static Map<EncodeHintType, Object> configMap;

	static {
		qrCodeWriter = new QRCodeWriter();
		configMap = new EnumMap<>(EncodeHintType.class);
		configMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
	}

	/**
	 * Constructor for this class
	 */
	private QrcodeGenerator() {

	}

	/**
	 * Method to generate QR Code
	 * 
	 * @param data
	 *            data to encode in the QR code
	 * @param version
	 *            {@link QrVersion} class for QR Code version
	 * @return array of byte containing QR Code in PNG format
	 * @throws QrcodeGenerationException
	 *             exceptions which may occur when encoding a QRcode using the
	 *             Writer framework.
	 * @throws IOException
	 *             exceptions which may occur when write to the byte stream fail
	 */
	public static byte[] generateQrCode(String data, QrVersion version)
			throws QrcodeGenerationException,  IOException {
		QrcodegeneratorUtils.verifyInput(data, version);
		configMap.put(EncodeHintType.QR_VERSION, version.getVersion());
	    BitMatrix byteMatrix = null;
		try {
			byteMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, version.getSize(), version.getSize(),
					configMap);
		} catch (WriterException | IllegalArgumentException exception) {
			throw new QrcodeGenerationException(QrcodeExceptionConstants.QRCODE_GENERATION_EXCEPTION.getErrorCode(),
					QrcodeExceptionConstants.QRCODE_GENERATION_EXCEPTION.getErrorMessage() + exception.getMessage(),
					exception);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(byteMatrix, QrcodeConstants.FILE_FORMAT, outputStream);
		return outputStream.toByteArray();

	}
}

package io.mosip.kernel.qrcode.generator.zxing;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import io.mosip.kernel.core.qrcodegenerator.exception.QrcodeGenerationException;
import io.mosip.kernel.core.qrcodegenerator.spi.QrCodeGenerator;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrcodeConstants;
import io.mosip.kernel.qrcode.generator.zxing.constant.QrcodeExceptionConstants;
import io.mosip.kernel.qrcode.generator.zxing.util.QrcodegeneratorUtils;

/**
 * Class which provides functionality to generate QR Code
 * 
 * @author Urvil Joshi
 *
 * @since 1.0.0
 */
@Component
public class QrcodeGeneratorImpl implements QrCodeGenerator<QrVersion> {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.kernel.qrcode.generator.zxing.QrCode#generateQrCode(java.lang.
	 * String, io.mosip.kernel.qrcode.generator.zxing.constant.QrVersion)
	 */
	@Override
	public byte[] generateQrCode(String data, QrVersion version) throws QrcodeGenerationException, IOException {
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

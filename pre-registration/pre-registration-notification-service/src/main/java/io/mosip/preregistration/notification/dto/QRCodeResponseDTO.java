package io.mosip.preregistration.notification.dto;

import lombok.Data;

/**
 * This Dto is used for geting reponse of generate QR code
 * 
 * @author Sanober Noor
 *@since 1.0.0
 */

@Data
public class QRCodeResponseDTO {
/**
 * 
 */
byte[] qrcode;
}

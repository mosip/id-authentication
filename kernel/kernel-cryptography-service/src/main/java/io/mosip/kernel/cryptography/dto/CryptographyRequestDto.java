package io.mosip.kernel.cryptography.dto;

import java.time.LocalDateTime;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptographyRequestDto {
String applicationId;
byte[] data;
String machineId;
LocalDateTime timeStamp;
}

package io.mosip.kernel.cryptography.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptographyRequestDto {
byte[] data;
@NotEmpty
String applicationId;
String machineId;
@NotEmpty
LocalDateTime timeStamp;
}

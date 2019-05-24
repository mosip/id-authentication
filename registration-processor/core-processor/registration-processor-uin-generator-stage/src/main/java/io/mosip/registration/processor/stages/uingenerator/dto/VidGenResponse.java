package io.mosip.registration.processor.stages.uingenerator.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import io.mosip.registration.processor.stages.uingenerator.idrepo.dto.ErrorDTO;
import lombok.Data;



@Data
public class VidGenResponse<T> {

	private String id;
	private String version;
//	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	private LocalDateTime responsetime; //= LocalDateTime.now(ZoneId.of("UTC"));
	private Object metadata;
	@NotNull
	@Valid
	private T response;
	private List<ErrorDTO> errors = new ArrayList<>();
}

package io.mosip.registration.processor.core.packet.dto.applicantcategory;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ApplicantCategory {

	private String applicantType;
	private List<DocumentCategory> documentCategory;

}

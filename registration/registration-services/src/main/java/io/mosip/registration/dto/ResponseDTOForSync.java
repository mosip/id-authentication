package io.mosip.registration.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ResponseDTOForSync {

	List<String> successJobs=new ArrayList<String>();
	List<String> errorJobs=new ArrayList<String>();
	
}

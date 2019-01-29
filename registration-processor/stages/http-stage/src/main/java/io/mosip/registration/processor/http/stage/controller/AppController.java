package io.mosip.registration.processor.http.stage.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.registration.processor.http.stage.dto.MessageDTO;

@RestController
public class AppController {

	@PostMapping(value="/test")
	public MessageDTO testProcess(@RequestBody MessageDTO messageDTO) {
		System.out.println("+++++request+++++ "+messageDTO);
		MessageDTO responseMessageDTO = new MessageDTO();
		responseMessageDTO.setRid(messageDTO.getRid());
		responseMessageDTO.setValid(true);
		responseMessageDTO.setInternalError(false);
		System.out.println("+++++response+++++ "+responseMessageDTO);
		return responseMessageDTO;
	}
}

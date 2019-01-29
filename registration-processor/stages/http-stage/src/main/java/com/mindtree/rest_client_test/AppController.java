package com.mindtree.rest_client_test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

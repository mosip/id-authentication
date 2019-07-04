package com.mindtree.camel_bridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.vertx.core.json.JsonObject;


@RestController
public class Verticle1Controller{
	
	@Autowired
	Verticle1 verticle1;
	
	@PostMapping("/initiate")
	public void initiateFlow(@RequestBody MessageDto messageDto){
		System.out.println("+++++++++++The requestDTO is+++++++++++ "+messageDto);
		JsonObject jsonObject = new JsonObject();
		jsonObject.put("rid", messageDto.getRid());
		jsonObject.put("isValid", messageDto.isValid());
		jsonObject.put("requestType", messageDto.isRequestType());
		verticle1.sendMessage(jsonObject);
	}
	
	@GetMapping("/health")
	public String checkHealth() {
		return "Up and running";
	}

}

package com.mindtree.camel_bridge;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Verticle1App {
	
	@Autowired
	Verticle1 verticle1;
	
	public static void main(String[] args) {
		SpringApplication.run(Verticle1App.class, args);
	}
	
	@PostConstruct
	public void deployVerticle() {
		verticle1.getEventBus();
	}

}

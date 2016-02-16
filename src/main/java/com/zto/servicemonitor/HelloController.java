package com.zto.servicemonitor;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class HelloController {
	@RequestMapping("/")
	public String index() throws Exception {
		 return "Greetings from Spring Boot!";
	}

}
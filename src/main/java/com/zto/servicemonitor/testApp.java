package com.zto.servicemonitor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class testApp {
	@RequestMapping("/test")
	public String index1() throws Exception {
		System.out.println("10.10.19.148:8080/test");
		return "10.10.19.148:8080/test";
	}

	@RequestMapping("/test2")
	public String index2() throws Exception {
		System.out.println("10.10.19.148:8080/test2");
		return "10.10.19.148:8080/test2";
	}
}

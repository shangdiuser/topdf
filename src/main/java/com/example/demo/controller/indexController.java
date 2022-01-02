package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *@author admin
 *@date 2022年1月1日
 */
@RestController
public class indexController {

	@RequestMapping("/index")
	public String index() {
		return "ok";
	}
	
}

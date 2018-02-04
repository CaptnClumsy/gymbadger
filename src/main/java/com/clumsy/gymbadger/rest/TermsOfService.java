package com.clumsy.gymbadger.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TermsOfService {
	@RequestMapping("/tos")
	public String handler() {
	    return "terms";
	}
}

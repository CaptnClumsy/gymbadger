package com.clumsy.gymbadger.rest;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {
	
	@RequestMapping(value = "/badges", method = RequestMethod.POST)
	public void UploadReceipts(@RequestParam("files[]") List<MultipartFile> files) throws Exception {
	    log.info(" Inside the upload receipts method "+files.size());
	    for(int i=0; i< files.size(); i++)
	    {
	        if(!files.get(i).isEmpty())
	        {
	            CommonsMultipartFile cm = (CommonsMultipartFile) files.get(i);
	            log.info(" Inside the file upload method "+cm.getOriginalFilename());   
	        }
	    }   
	}
}
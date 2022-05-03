package com.zurich.api.controller;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zurich.api.model.File;
import com.zurich.api.service.FileStorageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Dragisa Dragisic, 2022
 *
 */
@Tag(name = "File transfer async API", description = "Asynchronious file transfer with callback")
@RestController
public class AsyncFiletransferController {
	
	@Autowired
	private FileStorageService fileStorageService;

	Logger logger = LoggerFactory.getLogger(this.getClass());


	@Operation(summary = "Request a file from producer")
	@PostMapping("/scenario-5/files/request")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "202", description = "Accepted"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<File> requestFile(@Parameter(description = "Callback URL") @RequestParam(required = false, name = "callbackUrl") String callbackUrl, @Parameter(description = "Requested file") @RequestParam("file") String file) {
		if (callbackUrl != null && !callbackUrl.isEmpty()) {
			logger.info("Received callback URL: " + callbackUrl);
			logger.info("Requested file: " + file);
			
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/scenario-3/files/")
				.path(file)
				.toUriString();
		
        File file_obj = new File(file, fileDownloadUri, null, 0, callbackUrl);
        
        fileStorageService.addFileRecord(file_obj);

		return new ResponseEntity<>(HttpStatus.ACCEPTED);
	}

	@Operation(summary = "Consumer callback endpoint (save file and return 201)")
	@PostMapping("/scenario-5/files/callback")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<String> saveFile(@Parameter(description = "Requested file") @RequestParam("file") MultipartFile file) {
	
		// save file
		String fileName = fileStorageService.storeFile(file);
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/files/download/")
				.path(fileName)
				.toUriString();
		
		// confirm with 201 (set URI in Location)
	    return ResponseEntity.created(URI.create(fileDownloadUri)).body("Saved " + fileName);
	}

	@Operation(summary = "Consumer callback endpoint")
	@PostMapping("/scenario-6/files/callback-process")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public File dropFile(@Parameter(description = "Requested file") @RequestParam("file") MultipartFile file) {
	
		return fileStorageService.uploadFile(file, "");
	}

}

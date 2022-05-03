package com.zurich.api.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.zurich.api.exception.FileStorageException;
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
@Tag(name = "File consumer API", description = "File-Upload, Aufnahme von Files")
@RestController
public class FileConsumerController {
	
	@Autowired
	private FileStorageService fileStorageService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Operation(summary = "Upload file and processing")
	@PostMapping("/scenario-1/files")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Uploaded & processed"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public File processFile(@Parameter(description = "Callback URL") @RequestParam(required = false, name = "callbackUrl") String callbackUrl, @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
		if (callbackUrl != null && !callbackUrl.isEmpty()) {
			logger.info("Received callback URL: " + callbackUrl);
		}
        
		return fileStorageService.uploadFile(file, callbackUrl);
	}

	@Operation(summary = "Upload & save file with confirmation 201 (lazy processing)")
	@PostMapping("/scenario-2/files")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Uploaded"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<String> saveFile(@Parameter(description = "Callback URL") @RequestParam(required = false, name = "callbackUrl") String callbackUrl, @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
		if (callbackUrl != null && !callbackUrl.isEmpty()) {
			logger.info("Received callback URL: " + callbackUrl);
		}
		
		// save file
		String fileName = fileStorageService.storeFile(file);
		
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/scenario-3/files/")
				.path(fileName)
				.toUriString();
		
		// confirm with 201 (set URI in Location)
	    return ResponseEntity.created(URI.create(fileDownloadUri)).body("Saved " + fileName);
  		
	}


	@Operation(summary = "Upload and save files (bulk)")
	@PostMapping("/scenario-1/files/bulk-upload")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Uploaded"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public List<File> uploadMultipleFiles(@Parameter(description = "Callback URL") @RequestParam(required = false, name = "callbackUrl") String callbackUrl, @Parameter( description = "Files") @RequestParam("file") MultipartFile[] files) {
		if (callbackUrl != null && !callbackUrl.isEmpty()) {
			logger.info("Received callback URL: " + callbackUrl);
		}

		return Arrays.asList(files)
				.stream()
				.map(file -> fileStorageService.uploadFile(file, callbackUrl))
				.collect(Collectors.toList());
	}

	@Operation(summary = "Upload requested files")
	@PostMapping("/scenario-7/files/scheduled-upload")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Uploaded"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public List<File> uploadRequestedFiles() {
		
		
		List<File> requestedFiles = fileStorageService.getFiles();
		List<File> uploadedFiles = new ArrayList<>();
		// upload requested files from store (if exists!) to callback url
		requestedFiles.forEach((requestedFile) -> {
			if (requestedFile.getSize() > 0 && requestedFile.getFileType() != null && requestedFile.getFileCallbackUrl() != null && requestedFile.getFileCallbackUrl().startsWith("http://")) {
				try {
					FileInputStream input = new FileInputStream(requestedFile.getFileName());
					MultipartFile multipartFile = new MockMultipartFile("file", requestedFile.getFileName(), requestedFile.getFileType(), IOUtils.toByteArray(input));
					fileStorageService.uploadFile(multipartFile, requestedFile.getFileCallbackUrl());
					uploadedFiles.add(requestedFile);
					logger.info("Uploaded requested file: " + requestedFile.getFileName() + " calling " + requestedFile.getFileCallbackUrl());
				} catch (FileNotFoundException e) {
					throw new FileStorageException(e.getMessage());
				} catch (IOException e) {
					throw new FileStorageException(e.getMessage());
				}
			}
		});
		return uploadedFiles;
		

	}


}

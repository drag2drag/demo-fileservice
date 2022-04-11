package com.zurich.api.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@Tag(name = "Filetransfer API", description = "File-Transfer, Anzeige von File-Storage")
@RestController
public class FileController {
	
	@Autowired
	private FileStorageService fileStorageService;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public File uploadFile(MultipartFile file, String callbackUrl) {

		String fileName = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path("/files/download/")
				.path(fileName)
				.toUriString();
		
        File file_obj = new File(fileName, fileDownloadUri, file.getContentType(), file.getSize(), callbackUrl);
        
        fileStorageService.addFileRecord(file_obj);
		return file_obj;

	}

	@Operation(summary = "Upload file and confirmation (lazy processing)")
	@PostMapping("/files")
	@ResponseStatus(HttpStatus.CREATED)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Uploaded"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public String processFile(@Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
		return fileStorageService.processFile(file);
	}

	@Operation(summary = "Upload file and processing")
	@PutMapping("/files")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Uploaded & processed"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public File processAndStoreFile(@Parameter(description = "Callback URL") @RequestParam(required = false, name = "callbackUrl") String callbackUrl, @Parameter(description = "File to upload") @RequestParam("file") MultipartFile file) {
		return uploadFile(file, callbackUrl);
	}

	@Operation(summary = "Upload and store files (bulk)")
	@PostMapping("/files/bulk-upload")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Uploaded"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public List<File> uploadMultipleFiles(@Parameter(description = "Callback URL") @RequestParam(required = false, name = "callbackUrl") String callbackUrl, @Parameter( description = "Files") @RequestParam("file") MultipartFile[] files) {
		return Arrays.asList(files)
				.stream()
				.map(file -> uploadFile(file, callbackUrl))
				.collect(Collectors.toList());
	}

	@Operation(summary = "Download file")
	@GetMapping("/files/download/{fileName:.+}")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public ResponseEntity<Resource> downloadFile(@Parameter( description = "File") @PathVariable String fileName, HttpServletRequest request) {

		Resource resource = fileStorageService.loadFileAsResource(fileName);

		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type. Set application/octet-stream.");
		}

		// fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@Operation(summary = "Get files from DB")
	@GetMapping("/files")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public List<File> getFiles() {

		return fileStorageService.getFiles();
	}

	
	@Operation(summary = "Show file-storage")
	@GetMapping(value = "/files/storage", produces = {"plain/text"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public String getFilesFromStorage() {
		return fileStorageService.getStoredFiles();
	}



}

package com.zurich.api.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.zurich.api.exception.FileStorageException;

/**
 * @author Dragisa Dragisic, 2022
 *
 */
@Service
public class FileStorageService {

	@Value("${target.dir}")
	String targetDir;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public String storeFile(MultipartFile file) {

		// normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {

			if (fileName.contains("..")) {

				logger.error("Error storing " + fileName + ". File name contains invalid path sequence.");
				throw new FileStorageException("Invalid path sequence: " + fileName);
			}

			// copy file to the target location (replace existing file)
			Path targetLocation = Paths.get(targetDir).resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			logger.info("Success storing " + fileName);

			return fileName;

		} catch (IOException ex) {

			logger.error("Error storing " + fileName);
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);

		}
	}

	public String storeAndProcessFile(MultipartFile file) {
		// normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {

			if (fileName.contains("..")) {

				logger.error("Error storing " + fileName + ". File name contains invalid path sequence.");
				throw new FileStorageException("Invalid path sequence: " + fileName);
			}

			// copy file to the target location (replace existing file)
			Path targetLocation = Paths.get(targetDir).resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			logger.info("Success storing " + fileName);
			logger.info("Processing file " + fileName + " ...");
			try {
				Thread.sleep(30000);
			}
			catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
			logger.info("File " + fileName + " processed.");

			return fileName;

		} catch (IOException ex) {

			logger.error("Error storing " + fileName);
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);

		}
	}

	public String processFile(MultipartFile file) {
		// normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		if (fileName.contains("..")) {

			logger.error("Error storing " + fileName + ". File name contains invalid path sequence.");
			throw new FileStorageException("Invalid path sequence: " + fileName);
		}

		logger.info("Processing file " + fileName + " ...");
		try {
			Thread.sleep(30000);
		}
		catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		logger.info("File " + fileName + " processed.");

		return fileName;
	}

	public Resource loadFileAsResource(String fileName) {
		try {

			Path filePath = Paths.get(targetDir).resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());

			if (resource.exists()) {
				logger.info("Success retrieving " + fileName);
				return resource;
			} else {
				logger.error("Error retrieving " + fileName + ". File not found.");
				throw new FileStorageException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			logger.error("Error retrieving " + fileName + ". File not found.");
			throw new FileStorageException("File not found " + fileName, ex);
		}
	}

	public String getStoredFiles() {

		// execute command
		StringBuilder output = new StringBuilder();

		String command = "ls -lt " + targetDir; 
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("bash", "-c", command);

		try {
			Process process = processBuilder.start();


			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

			int exitVal = process.waitFor();
			if (exitVal == 0) {

				logger.info("Success executig " + command);

			} else {

				logger.debug("Error executing " + command);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return output.toString();

	}
}

package com.zurich.api.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.zurich.api.model.DataRecord;
import com.zurich.api.service.DataRecordService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * @author Dragisa Dragisic, 2022
 *
 */
@Tag(name = "Data-Record API", description = "Data-Record-Transfer")
@RestController
public class DataRecordController {

	private Logger logger = LoggerFactory.getLogger(DataRecordController.class);

	@Autowired
	DataRecordService dataRecordService;
	
	@GetMapping(value = "/data-records/{drid}", produces = {"application/json"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Not authorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public Optional<DataRecord> getDataRecord(@PathVariable("drid") int drid) {
		return dataRecordService.getDataPackage(drid);
	}
	
	@GetMapping(value = "/data-records", produces = {"application/json"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Not authorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public List<DataRecord> getDataRecords() {
		return dataRecordService.getDataRecords();
	}

	
	@PostMapping(value = "/data-records", produces = {"application/json"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "401", description = "Not authorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public DataRecord addDataRecord(@RequestBody DataRecord datarecord) {
		dataRecordService.addDataRecord(datarecord);
		return datarecord;
	}


	@PutMapping(value = "/data-records", produces = {"application/json"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Not authorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public DataRecord updateDataRecord(@RequestBody DataRecord datarecord) {
		dataRecordService.updateDataRecord(datarecord);
		return datarecord;
	}

	@DeleteMapping(value = "/data-records/{drid}", produces = {"application/json"})
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "401", description = "Not authorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden"),
			@ApiResponse(responseCode = "404", description = "Not found")
	})
	public List<DataRecord> deleteDataRecords(@PathVariable("drid")int drid) {
		return dataRecordService.deleteDataRecord(drid);
	}

	
}

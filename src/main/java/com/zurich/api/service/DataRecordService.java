package com.zurich.api.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zurich.api.dao.DataRecordRepo;
import com.zurich.api.model.DataRecord;

@Service
public class DataRecordService {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DataRecordRepo datarecordRepo;

	public List<DataRecord> getDataRecords() {
		return datarecordRepo.findAll();
	}
	
	
	public Optional<DataRecord> getDataPackage(int drid) {
		return datarecordRepo.findById(drid);
	}

	public DataRecord addDataRecord(DataRecord datarecord) {
		datarecordRepo.save(datarecord);
	    logger.info("Added data record:   " + datarecord.toString());
		return datarecord;
	}

	public DataRecord updateDataRecord(DataRecord datarecord) {
		datarecordRepo.save(datarecord);
	    logger.info("Updated data record:   " + datarecord.toString());
		return datarecord;
	}
	
	public List<DataRecord> deleteDataRecord(int drid) {
		DataRecord datarecord = datarecordRepo.getOne(drid);
		datarecordRepo.delete(datarecord);
	    logger.info("Deleted data record:   " + datarecord.toString());
		return datarecordRepo.findAll();
	}
	

}

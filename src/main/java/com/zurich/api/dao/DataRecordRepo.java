package com.zurich.api.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zurich.api.model.DataRecord;


/**
 * @author Dragisa Dragisic, 2022
 *
 */
@Repository
public interface DataRecordRepo extends JpaRepository<DataRecord, Integer> {


	List<DataRecord> findAll();

	Set<DataRecord> findDataRecordByKey1(String key1);



}

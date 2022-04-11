package com.zurich.api.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zurich.api.model.File;

@Repository
public interface FileRepo extends JpaRepository<File, Integer> {
	
	List<File> findAll();

}

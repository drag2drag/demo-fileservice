package com.zurich.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity(name = "datarecord")
public class DataRecord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "datarecord_id")
	private int drid;
	
	@Column(name = "key_1")
	@NotNull
	@Size(max = 30)
	private String key1;

	@Column(name = "key_2")
	@NotNull
	@Size(max = 30)
	private String key2;
		
	@Column(name = "key_3")
	@NotNull
	private String key3;


	public DataRecord() {
		super();
	}

	public DataRecord(int drid, @NotNull @Size(max = 30) String key1, @NotNull @Size(max = 30) String key2,
			@NotNull String key3) {
		super();
		this.drid = drid;
		this.key1 = key1;
		this.key2 = key2;
		this.key3 = key3;
	}

	public int getDrid() {
		return drid;
	}

	public void setDrid(int drid) {
		this.drid = drid;
	}

	public String getKey1() {
		return key1;
	}

	public void setKey1(String key1) {
		this.key1 = key1;
	}

	public String getKey2() {
		return key2;
	}

	public void setKey2(String key2) {
		this.key2 = key2;
	}

	public String getKey3() {
		return key3;
	}

	public void setKey3(String key3) {
		this.key3 = key3;
	}

	@Override
	public String toString() {
		return "DataRecord [drid=" + drid + ", key1=" + key1 + ", key2=" + key2 + ", key3=" + key3 + "]";
	}
	



}

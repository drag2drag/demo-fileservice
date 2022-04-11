package com.zurich.api.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Dragisa Dragisic, 2022
 *
 */

@Entity(name = "file")
public class File {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "datarecord_id")
	private int drid;


	@Column(name = "file_name")
	@NotNull
	@Size(max = 30)
	private String fileName;
	
	@Column(name = "file_uri")
	private String fileDownloadUri;
	
	@Column(name = "file_type")
	private String fileType;
	
	@Column(name = "file_size")
	private long size;
	
	@Column(name = "file_callback_url")
	private String fileCallbackUrl;
	
	
	public File() {
		super();
	}

	public File(String fileName, String fileDownloadUri, String fileType, long size, String fileCallbackUrl) {
		super();
		this.fileName = fileName;
		this.fileDownloadUri = fileDownloadUri;
		this.fileType = fileType;
		this.size = size;
		this.fileCallbackUrl = fileCallbackUrl;
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileDownloadUri() {
		return fileDownloadUri;
	}
	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}

	public String getFileCallbackUrl() {
		return fileCallbackUrl;
	}

	public void setFileCallbackUrl(String fileCallbackUrl) {
		this.fileCallbackUrl = fileCallbackUrl;
	}

	@Override
	public String toString() {
		return "File [fileName=" + fileName + ", fileDownloadUri=" + fileDownloadUri + ", fileType=" + fileType
				+ ", size=" + size + ", callbackUrl=" + fileCallbackUrl + "]";
	}
	
	

}

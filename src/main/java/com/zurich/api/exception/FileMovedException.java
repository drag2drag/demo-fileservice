package com.zurich.api.exception;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(value = HttpStatus.SEE_OTHER)
public class FileMovedException extends ResponseStatusException {

	private static final long serialVersionUID = 1L;

	public FileMovedException(String message) {
		super(HttpStatus.SEE_OTHER, message);
    }
    
    @Override
    public HttpHeaders getResponseHeaders() {
        // set Location
    	URI location = URI.create("http://localhost:8999/scenario-3/files?fileName=login.pdf");
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);    
        return responseHeaders; 
   }
}

package org.exi.demo.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class ApiError {

	  private LocalDateTime timestamp;
	  private int statusCode;
	  private String statusName;
	  private String targetUrl;
	  private Map<String, String> fieldErrors;
}

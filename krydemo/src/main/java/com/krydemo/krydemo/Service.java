
package com.krydemo.krydemo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Service {
	
	  private static final AtomicInteger COUNTER = new AtomicInteger();
	  private String id;
	  private String name;
	  private String url;
	  private String status;
	  private String lastCheck;

	  public Service(String name, String url, String lastCheck, String status, String id) {		  
		this.id = id;		
	    this.name = name;
	    this.status = status;
	    this.url = url;
	    this.lastCheck = lastCheck;
	  }
	  
	  public Service createNewService(String name, String url) {
		  String status = "OK";
		  String lastCheck = LocalDateTime.now().toString();
		  String id = UUID.randomUUID().toString();

		return new Service(name, url, lastCheck, status, id);
		
	  }

	  public Service() {
		  this.id = UUID.randomUUID().toString();
	  }

	  public String getName() {
		  return name;
	  }
	  public void setName(String name) {
		  this.name = name;
	 }

	  public String getUrl() {
		  return url;
	  }
	  public void setUrl(String url) {
		  this.url = url;
	 }

	  public String getId() {
		  return id;
	  }
	  
	  public String getLastCheck() {
		  return lastCheck;
	  }
	  
	  public void setLastCheck(String lastCheck) {
		  this.lastCheck = lastCheck;
	  }

	  public String getStatus() {
		    return status;
	  }
	  public void setStatus(String status) {
	    this.status = status;
	  }
	}

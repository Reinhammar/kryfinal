
package com.krydemo.krydemo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Service {
	
	  private static final AtomicInteger COUNTER = new AtomicInteger();
	  private int id;
	  private String name;
	  private String url;
	  private String status;
	  private LocalDateTime lastchecked;

	  public Service(String name, String url) {
	    this.id = COUNTER.getAndIncrement();
	    this.name = name;
	    this.status = "OK";
	    this.url = url;
	    this.lastchecked = LocalDateTime.now();
	  }	  

	  public Service() {
		  this.id = COUNTER.getAndIncrement();
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

	  public int getId() {
		  return id;
	  }
	  
	  public LocalDateTime getLastChecked() {
		  return lastchecked;
	  }
	  
	  public void setLastChecked(LocalDateTime lastChecked) {
		  this.lastchecked = lastChecked;
	  }

	  public String getStatus() {
		    return status;
	  }
	  public void setStatus(String status) {
	    this.status = status;
	  }
	}

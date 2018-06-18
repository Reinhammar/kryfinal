package com.krydemo.krydemo;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder; 

public class GsonService {

	   public void main(String[] args) { 
	      String jsonString = "{\"name\":\"Mahesh\", \"age\":21}"; 
	      
	      GsonBuilder builder = new GsonBuilder(); 
	      builder.setPrettyPrinting(); 
	      
	      Gson gson = builder.create(); 
	      
	      Service service = gson.fromJson(jsonString, Service.class); 
	      System.out.println(service);    
	      
	      jsonString = gson.toJson(service); 
	      System.out.println(jsonString);  
	      
	   }
	   
	   public void addNew(Service service) {
		   try {
			  
			PrintWriter writer = new PrintWriter("database.json", "UTF-8");
			writer.print(service);
			writer.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
	   }
	} 


package com.krydemo.krydemo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class MyFirstVerticle extends AbstractVerticle {
	// Store our services
	private Map<String, Service> services = new LinkedHashMap<>();
	private Map<String, Service> newServices = new LinkedHashMap<>();	

	GsonService gsonService = new GsonService();
	JSONArray allServices;
	int numberOfServices;
	String dbPath = "C:/Users/TobiasReinhammar/database2.json"; //Change to relative path
	
	@Override
	public void start(Future<Void> fut) {
	 
     try {
    	 newServices.clear();
    	 FileReader fw = new FileReader(dbPath);
	     JSONTokener tokener = new JSONTokener(fw);
	     
	     JSONObject root = new JSONObject(tokener);
	     
	     allServices = (JSONArray) root.get("services");
	     numberOfServices = allServices.length();
	     JSONObject item;     
	     String name;
	     String url;
	     String lastCheck;
	     String status;
	     Service currentObj;
	     
	     for(int i = 0; i < numberOfServices; i++) { 
	    	  
	    	  item = allServices.getJSONObject(i);
	    	  
	    	  String id = (String) item.get("id");	
	    	  name = (String) item.get("name");
	    	  url = (String) item.get("url");
	    	  lastCheck = (String) item.get("lastCheck");
	    	  status = (String) item.get("status");
	    	  currentObj = new Service(name, url, lastCheck, status, id);
	    	  
	    	  newServices.put(currentObj.getId(), currentObj);  
	    	  }
	         services = newServices;
     }
     catch(Exception e) {
    	 e.printStackTrace();
     }    
     
	 Router router = Router.router(vertx);
	 router.route("/").handler(routingContext -> {
	   HttpServerResponse response = routingContext.response();
	   response
	       .putHeader("content-type", "text/html")
	       .end("<h1>Hello from my first Vert.x 3 application</h1>");
	 });

	 // Serve static resources from the /assets directory
	 router.route("/assets/*").handler(StaticHandler.create("assets"));
	 router.get("/api/services").handler(this::getAll);
	 router.route("/api/services*").handler(BodyHandler.create());
	 router.post("/api/services").handler(this::addOne);
	 router.delete("/api/services/:id").handler(this::deleteOne);
	 router.put("/api/services/:id").handler(this::poll);
	 

	 vertx	    
	     .createHttpServer()
	     .requestHandler(router::accept)	     
	     .listen(
	         // Retrieve the port from the configuration,
	         // default to 8080.
	         config().getInteger("http.port", 8080),
	         result -> {
	           if (result.succeeded()) {
	             fut.complete();
	           } else {
	             fut.fail(result.cause());
	           }
	         }
	     ); 
	}
	
	private void poll(RoutingContext routingContext) {		
	     
	     try {	  
	    	 String id = routingContext.request().getParam("id");	    	 
			     String  url = services.get(id).getUrl();
			     WebClient client = WebClient.create(vertx);
	     
		   	  	 client
		   	  	 	.get(8080, "localhost", url)
		   	  	 	.send(ar -> {	   	  	 	
		   	  	 		if(ar.succeeded()) {	   	  	 			
		   	  	 			HttpResponse<Buffer> response = ar.result();	   	  	 			
		   	  	 			String returnString = updateOne(id, response.statusMessage());	 
			   	  	 		routingContext.response().setStatusMessage(returnString);
	
		   	  	 		}
		   	  	 		else {	   
		   	  	 			String returnString = updateOne(id, "Fail");
			   	  	 		routingContext.response().setStatusMessage(returnString);
		   	  	 		}	
		   	  	 	});
	    	 
	   	 
		 } catch(Exception e) {
			 e.printStackTrace();
		 }		   
	 			routingContext.response().setStatusCode(204).end();

	}
	
	private void getAll(RoutingContext routingContext) {
		
		  routingContext.response()
		      .putHeader("content-type", "application/json; charset=utf-8")
		      .end(Json.encodePrettily(services.values()));
		
	}
	private void addOne(RoutingContext routingContext) {
		  
		  JsonObject serObj = new JsonObject();
		  try {
			  serObj = (JsonObject) routingContext.getBodyAsJson();
			  
			  String name = (String) serObj.getValue("name", "");
			  String url = (String) serObj.getValue("url", "");
			  JSONObject newService = new JSONObject();
			  newService.put("name", name);
			  newService.put("url", url);
			  newService.put("status", "OK");
			  newService.put("lastCheck", LocalDateTime.now());
			  newService.put("id",  UUID.randomUUID().toString());
			  routingContext.response().setStatusMessage(""+newService);
			  
			  FileReader fw = new FileReader(dbPath);

			  JSONTokener tokener = new JSONTokener(fw);		  
			  
			  JSONObject root = new JSONObject(tokener);
			  JSONArray allServices = (JSONArray) root.get("services");
			  JSONObject json = new JSONObject();		  
			  allServices.put(newService);
		  
			  json.put("services", allServices);	  
			  
			  BufferedWriter bw = new BufferedWriter(new FileWriter(dbPath));
			  bw.write(json.toString());
			  bw.close();
			  Service newServiceObj = new Service(newService.getString("name"),newService.getString("url"), newService.get("lastCheck").toString(), newService.getString("status") ,newService.getString("id") );
			  services.put(newServiceObj.getId(), newServiceObj);
			  routingContext.response()
		      .setStatusCode(201)
		      .putHeader("content-type", "application/json; charset=utf-8")
		      .end(Json.encodePrettily(newServiceObj));
		  }
		  catch(Exception e){
			  e.printStackTrace();
		  }
	}
	
	private void deleteOne(RoutingContext routingContext) {
		  String id = routingContext.request().getParam("id");
		  
		  String message = id;
		  if (id == null) {
		    routingContext.response().setStatusCode(400).end();
		  } else {
			  			  
			  try {
				  JSONTokener tokener = new JSONTokener(new FileReader("C:/Users/TobiasReinhammar/database2.json"));		     
				  JSONObject root = new JSONObject(tokener);		     
				  JSONArray allServices = (JSONArray) root.get("services");
				  int numberOfServices = allServices.length();
				  JSONArray newService =new JSONArray();
				  JSONObject item; 
				  String currId;
				  for(int i = 0; i < numberOfServices; i++) {
					  
					  item = allServices.getJSONObject(i);		    	  
					  currId = (String) item.get("id");
					  
					  if(id.equals(currId)) {
						  //allServices.remove(i);
						  message = ""+item;
					  }	else {
						  newService.put(item);
					  }
				  }
				  JSONObject json = new JSONObject();
				  json.put("services", newService);
				  BufferedWriter bw = new BufferedWriter(new FileWriter("C:/Users/TobiasReinhammar/database2.json"));
					
				  bw.write(json.toString());
				  bw.close();
		     services.remove(id);
			 }
		     catch(Exception e) {
		     }
		  }
		  routingContext.response().setStatusCode(204).setStatusMessage(message).end();
		}
	
	private String updateOne(String id, String newStatus) {
		String returnString = "";
		  try {
			  
			  FileReader fw = new FileReader(dbPath);
			  JSONTokener tokener = new JSONTokener(fw);  
			  
			  JSONObject root = new JSONObject(tokener);
			  JSONArray allServices = (JSONArray) root.get("services");
			  JSONArray updatedServices = new JSONArray();	    	  
	    	  JSONObject updatedService = new JSONObject();
	    	  JSONObject item = new JSONObject();
	    	  
				  String name; 
				  String url;
				  String lastCheck;
				  String oldId;					  
				  String oldStatus;
				  
				  numberOfServices = allServices.length();
				  Service currentObj;

				  for(int i = 0; i < numberOfServices; i++) {    	 
			    	  item = allServices.getJSONObject(i);
			    	  String currId = (String) item.get("id");
			    	  if(id.equals(currId)) {
			    		   
			    		  
			    		  name = (String) item.get("name");
				    	  url = (String) item.get("url");
				    	  oldStatus = (String) item.get("status");
				    	  lastCheck = LocalDateTime.now().toString();
				    	  returnString = "id: " + oldStatus + newStatus;
				    	  
				    	  updatedService.put("name", name);
				    	  updatedService.put("url", url);
				    	  updatedService.put("status", newStatus);
				    	  updatedService.put("lastCheck", LocalDateTime.now());
				    	  updatedService.put("id",  currId);
				    	  updatedServices.put(updatedService);
				    	  Service oldService = services.get(id);
				    	  currentObj = new Service(services.get(currId).getName(), services.get(currId).getUrl(), LocalDateTime.now().toString(), newStatus, currId);
				    	  services.remove(id);
				    	  services.put(id, currentObj);
			    	  }else {
			    		  updatedServices.put(item);
			    	  } 
			      } 		  
		  JSONObject json = new JSONObject(); 
		  json.put("services", updatedServices);	  
		  
		 BufferedWriter bw = new BufferedWriter(new FileWriter(dbPath));
		
		 bw.write(json.toString());
		 bw.close();
		 	 
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return returnString;
	}
	
}

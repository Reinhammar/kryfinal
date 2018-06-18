package com.krydemo.krydemo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
 
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import java.io.FileReader;
import java.util.Iterator;

public class MyFirstVerticle extends AbstractVerticle {
	// Store our services
	private Map<Integer, Service> services = new LinkedHashMap<>();
	private ArrayList<Service> serviceArray = new ArrayList<>();
	private JsonArray jsonArray = new JsonArray();
	
	
	@Override
	public void start(Future<Void> fut) {
	 //createSomeData();
     JSONParser parser = new JSONParser();
     try {
     Object obj = parser.parse(new FileReader(
             "database.json"));
     System.out.println();
     JSONObject jsonObject = (JSONObject) obj;
     JSONArray allServices = (JSONArray) jsonObject.get("service");
     Iterator iterator = allServices.iterator();
     while(iterator.hasNext()) {
    	 int id = (int) jsonObject.get("id");
    	 String name = (String) jsonObject.get("name");
    	 String url = (String) jsonObject.get("url");
    	 String status = (String) jsonObject.get("status");
    	 LocalDateTime lastChecked = (LocalDateTime) jsonObject.get("lastChecked");
   	  	 Service current = new Service(name, url);

    	 services.put(id, current);
     }
     }
     catch(Exception e) {
    	 e.printStackTrace();
     }    
     
	 Service stuff = new Service("cool beans","https://Kry.coolbeans.se");   
	 GsonService gsonService = new GsonService();
	 gsonService.addNew(stuff);
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
	
	private void getAll(RoutingContext routingContext) {
		  routingContext.response()
		      .putHeader("content-type", "application/json; charset=utf-8")
		      .end(Json.encodePrettily(jsonArray));
	}
	private void addOne(RoutingContext routingContext) {
		  final Service service = Json.decodeValue(routingContext.getBodyAsString(),
		      Service.class);
		  services.put(service.getId(), service);
		  routingContext.response()
		      .setStatusCode(201)
		      .putHeader("content-type", "application/json; charset=utf-8")
		      .end(Json.encodePrettily(service));
		}

	
	// Create some product
	private void createSomeData() {
	  Service doctor = new Service("Doctor site", "/api/doctor");
	  services.put(doctor.getId(), doctor);
	  Service patient = new Service("Patient site", "/api/patient");
	  services.put(patient.getId(), patient);
	}
}
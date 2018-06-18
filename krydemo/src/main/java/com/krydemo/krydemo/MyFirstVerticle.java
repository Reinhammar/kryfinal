package com.krydemo.krydemo;

import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class MyFirstVerticle extends AbstractVerticle {
	// Store our services
	private Map<Integer, Service> services = new LinkedHashMap<>();
	@Override
	public void start(Future<Void> fut) {
	 createSomeData();
	 
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
		      .end(Json.encodePrettily(services.values()));
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
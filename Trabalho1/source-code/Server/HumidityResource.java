/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  UFRJ / COPPE / PESC
 * PROJECT       :  CPS731 :: IoT Lab
 * FILENAME      :  TemperatureResource.java
 *
 * This file is an implementation work, part of a postgraduate course.
 * Course website (PT-BR):  https://sites.google.com/cos.ufrj.br/lab-iot
 * **********************************************************************
 * 
 * This code was written based on the Californium project examples
 * (COAP Server / Resources).

 * #L%
 */
package br.ufrj.cos.iotlab2019.server.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class is used to define a new resource type (Humidity)
 * based on DHT11 readings using the GPIO interface of a Raspberry
 * Pi model B. The sensor readings are collected via Python script.
 *
 * @author Egberto Rabello
 */
public class HumidityResource extends CoapResource {
	
	// Constructor
	public HumidityResource(String name) {
		
		// Define resource name
		super(name);
		
		// Create a Timer object to get DHT11 readings each 3 seconds
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(this), 0, 3000);
	}
		
	// Variables
	private String measure = "";
	
	// Method to update readings
	private class UpdateTask extends TimerTask {
		
		// Private Variables
		private CoapResource mCoapRes;
		
		// Private method to read sensor data using a Python script
		private String getSensorData() {
			String sensorData = null;
			try {
				Runtime rt = Runtime.getRuntime();
				// Execute script dht.py with two arguments
				// - 1: sensor model (11)
				// - 2: GPIO pin (4)
				Process p = rt.exec("python dht.py 11 4");
				BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
				sensorData = bri.readLine();
				bri.close();
				p.waitFor();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			// This method returns NULL if no data is available (fail reading data)
			return sensorData;
		}
		
		public UpdateTask(CoapResource coapRes) {
		mCoapRes = coapRes;
		}
		
		// Custom method to update resource data (measure)
		@Override public void run() {
			String newMeasure = getSensorData().split("#")[1];
			
			if (!newMeasure.equals(measure) && (newMeasure != null)) {
				measure = newMeasure;
				mCoapRes.changed();
			}
		}
	}
	
	// Custom method to handle GET requests
	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.CONTENT,
				measure,
				MediaTypeRegistry.TEXT_PLAIN);
	}
}

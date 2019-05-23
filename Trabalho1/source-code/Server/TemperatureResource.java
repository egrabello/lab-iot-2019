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

import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

/**
 * This class is used to define a new resource type (Temperature)
 * based on DHT11 sensor readings.
 *
 * @author Egberto Rabello
 */
public class TemperatureResource extends CoapResource {
	
	// Constructor
	public TemperatureResource(String name, SensorDHT11 sensor) {
		
		// Define resource name
		super(name);
		sensorDHT11 = sensor;
		
		// Create a Timer object to get DHT11 readings each 3 seconds
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(this), 0, 3000);
	}
		
	// Variables
	private String measure = "";
	private SensorDHT11 sensorDHT11;
	
	// Method to update readings
	private class UpdateTask extends TimerTask {
		
		// Private Variables
		private CoapResource mCoapRes;
		
		// Method to update readings
		public UpdateTask(CoapResource coapRes) {
		mCoapRes = coapRes;
		}
		
		// Custom method to update resource data (measure)
		@Override public void run() {
			String newMeasure = sensorDHT11.getTemperature();
			
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

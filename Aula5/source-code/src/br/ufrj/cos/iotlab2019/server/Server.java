/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  UFRJ / COPPE / PESC
 * PROJECT       :  CPS731 :: IoT Lab
 * FILENAME      :  Server.java
 *
 * This file is an implementation work, part of a postgraduate course.
 * Course website (PT-BR):  https://sites.google.com/cos.ufrj.br/lab-iot
 * **********************************************************************
 * 
 * This code was written based on the Californium project examples
 * (COAP Server / Resources).

 * #L%
 */
package br.ufrj.cos.iotlab2019.server;

import org.eclipse.californium.core.CoapServer;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufrj.cos.iotlab2019.server.resources.HumidityResource;
import br.ufrj.cos.iotlab2019.server.resources.SensorDHT11;
import br.ufrj.cos.iotlab2019.server.resources.StatusLEDsResource;
import br.ufrj.cos.iotlab2019.server.resources.TemperatureResource;

/**
 * This class is used set up a CoAp server that handles GET and PUT
 * requests to interact with a custom resources (LEDs and DHT11 sensors).
 *
 * @author Egberto Rabello
 */
public class Server extends CoapServer {

	// Logger and ID
	private static final Logger s_logger = LoggerFactory.getLogger(Server.class);
    private static final String APP_ID = "br.ufrj.cos.iotlab2019.server_osgi";
    Server server;
	
    // Activator
    protected void activate(ComponentContext componentContext) {

    	// Define CoAP server
		server = new Server();
		
		// create a sensor object
		SensorDHT11 sensor = new SensorDHT11();
		
		// add a "temperature" resource of TemperatureResource type as observable
		TemperatureResource tempRes = new TemperatureResource("temperature", sensor);
		tempRes.setObservable(true);
		tempRes.getAttributes().setObservable();
		server.add(tempRes);
		
		// add a "humidity" resource of HumidityResource type as observable
		HumidityResource humiRes = new HumidityResource("humidity", sensor);
		humiRes.setObservable(true);
		humiRes.getAttributes().setObservable();
		server.add(humiRes);
		
		// add a "leds" resource of StatusLEDsResource type
		StatusLEDsResource statusLEDs = new StatusLEDsResource("leds");
		server.add(statusLEDs);
		
		// start CoAP server
		server.start();
    	
    	s_logger.info("Bundle " + APP_ID + " has started!");
    	s_logger.info("CoapServer instance has started!");
    	s_logger.info("Make sure the GPIO circuit is properly set up.");
    	s_logger.debug(APP_ID + ": This is a debug message.");

    }
    
    // Deactivator
    protected void deactivate(ComponentContext componentContext) {

        server.stop();
    	s_logger.info("Bundle " + APP_ID + " has stopped!");

    }

}

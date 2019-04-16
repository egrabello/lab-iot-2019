/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  UFRJ / COPPE / PESC
 * PROJECT       :  CPS731 :: IoT Lab
 * FILENAME      :  StatusLEDsResource.java
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

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import static org.eclipse.californium.core.coap.CoAP.ResponseCode.*;

/**
 * This class is used to define a new resource type (StatusLEDsResource)
 * based on an object of custom class (StatusLEDs) that interacts with
 * the GPIO interface of a Raspberry Pi model B to represent its status
 * using two LEDs (red and green).
 *
 * @author Egberto Rabello
 */
public class StatusLEDsResource extends CoapResource {
	// Variables
	StatusLEDs myLEDs;
	
	// Constructor
	public StatusLEDsResource (String name) {
		
		// define resource name
		super(name);
		
		// create a StatusLEDs object
		myLEDs = new StatusLEDs();
	}
	
	// custom method to handle GET requests
	@Override
	public void handleGET(CoapExchange exchange) {
		
		// return myLEDs current status
		exchange.respond(ResponseCode.CONTENT,
				String.valueOf("STATUS: "+ myLEDs.getState()),
				MediaTypeRegistry.TEXT_PLAIN);
	}
	
	// custom method to handle PUT requests
	@Override
    public void handlePUT(CoapExchange exchange) {
        
		// get the request's content to be used further
		byte[] payload = exchange.getRequestPayload();
        
		// update myLEDs status with the request's content
		try {
            String value = new String(payload, "UTF-8");
            myLEDs.setState(Integer.parseInt(value));
            exchange.respond(CHANGED, value);
		} catch (Exception e) {
            
			// return "bad request" if the request's content is invalid
			e.printStackTrace();
            exchange.respond(BAD_REQUEST, "ERROR: Invalid Integer!");
        }
    }
}

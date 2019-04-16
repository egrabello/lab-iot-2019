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

import br.ufrj.cos.iotlab2019.server.resources.DHT11Resource;
import br.ufrj.cos.iotlab2019.server.resources.StatusLEDsResource;

/**
 * This class is used set up a CoAp server that handles GET and PUT
 * requests to interact with a custom resources (LEDs and DHT11 sensors).
 *
 * @author Egberto Rabello
 */
public class Server extends CoapServer {

	// Operation mode: 1= Sensor only; 2= LEDs only; 0= Both (Sensor and LEDs)
	static int opMode;
	
	// Constructor with port number and operation mode
	public Server(int pOpMode, int pPort) {
		super(pPort);
		opMode = pOpMode;
	}
		
	// Main method
	public static void main(String[] args) {
		
		// Define CoAP server
		Server server;
		
		// Check args[] to define CoAP server operation mode and port number
		if (args.length == 0) {
			// create CoAP server with default parameters (opMode=0; port=5683) 
			server = new Server(0, 5683);
		} else if (args.length == 1) {
			// create CoAP server with custom opMode and default port number (5683) 
			server = new Server(Integer.parseInt(args[0]), 5683);
		} else {
			// create CoAP server with custom opMode and port number
			server = new Server(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		}
			
		if (opMode != 2) {
			// add a "dht11" resource of DHT11Resource type as observable
			DHT11Resource dht11Res = new DHT11Resource("dht11");
			dht11Res.setObservable(true);
			dht11Res.getAttributes().setObservable();
			server.add(dht11Res);
		}
		
		if (opMode != 1) {
			// add a "leds" resource of StatusLEDsResource type
			StatusLEDsResource statusLEDs = new StatusLEDsResource("leds");
			server.add(statusLEDs);
		}
		
		// start CoAP server
		server.start();
		
		// display disclaimer on console
        System.out.println(" >>> CoapServer instance started with operation mode " + opMode + "!");
        System.out.println(" >>> Make sure the GPIO circuit is properly set up.");
        System.out.println(" >>> Press CTRL-C to exit.");
	}

}

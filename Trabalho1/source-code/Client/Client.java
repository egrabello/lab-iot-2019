/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  UFRJ / COPPE / PESC
 * PROJECT       :  CPS731 :: IoT Lab
 * FILENAME      :  ClientCLI.java
 *
 * This file is an implementation work, part of a postgraduate course.
 * Course website (PT-BR):  https://sites.google.com/cos.ufrj.br/lab-iot
 * **********************************************************************
 * 
 * This code was written based on the Californium project examples
 * (COAP Client).

 * #L%
 */
package br.ufrj.cos.iotlab2019.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

/**
 * This class implements a Command Line Interface (CLI) to set up a CoAp
 * client that make requests to observe a remote resources (DHT11 sensors
 * and LEDs).
 *
 * @author Egberto Rabello
 */
public class Client {

	// Main method
	public static void main(String[] args) throws Exception {
		
		// Get input parameters from user
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		final String sensorHost;
		final String sensorPort;
		final String ledsHost;
		final String ledsPort;
		System.out.println(">> Please enter the input parameters below:");
		System.out.print(">> Operation Mode (1- Centralized Host; 2- Distributed Hosts): ");
		final int opMode = Integer.parseInt(reader.readLine());
		if (opMode == 1) {
			System.out.print(">> Host: ");
			sensorHost = reader.readLine();
			System.out.print(">> Port: ");
			sensorPort = reader.readLine();
			ledsHost = sensorHost;
			ledsPort = sensorPort;
		} else {
			System.out.print(">> Sensor Host: ");
			sensorHost = reader.readLine();
			System.out.print(">> Sensor Port: ");
			sensorPort = reader.readLine();
			System.out.print(">> LEDs Host: ");
			ledsHost = reader.readLine();
			System.out.print(">> LEDs Port: ");
			ledsPort = reader.readLine();
		}	
		System.out.print(">> Temperature Threshold: ");
		final Float temperatureThreshold = Float.parseFloat(reader.readLine());
		System.out.print(">> Humidity Threshold: ");
		final Float humidityThreshold = Float.parseFloat(reader.readLine());
		
		// Create CoAP clients
		final CoapClient sensorClient = new CoapClient("coap://" + sensorHost + ":" + sensorPort + "/dht11");
		final CoapClient ledsClient = new CoapClient("coap://" + ledsHost + ":" + ledsPort + "/leds");
		
		// Display disclaimer on console
		System.out.println();
		System.out.println(">> CoAP client started!");
		System.out.println(">> Press CTRL-C to exit.");
		
		// Establish observe relation to sensor
		sensorClient.observeAndWait(new CoapHandler() {
			
			// Variables (measures)
			Float temperature;
			Float humidity;
			
			// Method to display status on console with timestamp
			void cliOutput(String text) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				System.out.println(">> " + dateFormat.format(new Date()) + " --> " + text);
			}
			
			// Method to set status on output server and console
			void setStatus(int status){
				
				String outputString = "Temperature= " + String.format("%.02f", temperature) + "*,  Humidity= " + String.format("%.02f", humidity) + "%,  ";
				switch (status) {
		            case 0:  outputString += "Red LED= OFF,  Green LED= OFF";
		                     break;
		            case 1:  outputString += "Red LED= ON,  Green LED= OFF";
		                     break;
		            case 2:  outputString += "Red LED= OFF,  Green LED= ON";
		                     break;
		            case 3:  outputString += "Red LED= BLINK,  Green LED= BLINK";
		                     break;
				}
				
				ledsClient.put(Integer.toString(status), MediaTypeRegistry.TEXT_PLAIN);
				cliOutput(outputString);
			}
			
			// Define action taken when observed resource changes
			@Override
			public void onLoad(CoapResponse response) {

				// Handle response payload
				String[] readings = response.advanced().getPayloadString().split("#");
				if (readings.length == 2) {
					temperature = Float.parseFloat(readings[0]);
					humidity = Float.parseFloat(readings[1]);
					if ((temperature > temperatureThreshold) && (humidity > humidityThreshold)) {
						setStatus(3);
					} else if (humidity > humidityThreshold) {
						setStatus(2);
					} else if (temperature > temperatureThreshold) {
						setStatus(1);
					} else {
						setStatus(0);
					}
				}
				
			}

			// Handle connection error to output server
			@Override
			public void onError() {
				cliOutput("Erro na conexao com o servidor");
			}
		});
		
		// keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }
	}

}

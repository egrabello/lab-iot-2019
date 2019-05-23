/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  UFRJ / COPPE / PESC
 * PROJECT       :  CPS731 :: IoT Lab
 * FILENAME      :  SensorDHT11.java
 *
 * This file is an implementation work, part of a postgraduate course.
 * Course website (PT-BR):  https://sites.google.com/cos.ufrj.br/lab-iot
 * **********************************************************************

 * #L%
 */
package br.ufrj.cos.iotlab2019.server.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * This class is used to get Humidity and Temperature readings from
 * a DHT11 sensor using the GPIO interface of a Raspberry Pi model
 * B. The sensor readings are collected via Python script.
 *
 * @author Egberto Rabello
 */
public class SensorDHT11 {

	// Constructor
	public SensorDHT11() {
		getReadings();
	}
	
	// Variables
	private String temperature;
	private String humidity;
	private Date stamp;
	
	// Private method to read sensor data using a Python script
	private void getReadings() {
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
		// Update sensor readings
		if (sensorData != null && !sensorData.isEmpty()) {
			temperature = sensorData.split("#")[0];
			humidity = sensorData.split("#")[1];
			stamp = new Date(System.currentTimeMillis());
		}
	}
	
	// Public method to get the actual value for Temperature
	public String getTemperature() {
		// Update readings if Stamp is older than 3 seconds
		Date currentDateTime = new Date(System.currentTimeMillis());
		if (((currentDateTime.getTime() - stamp.getTime())/1000) > 3) {
			getReadings();
		}
		return temperature;
	}
	
	// Public method to get the actual value for Humidity
		public String getHumidity() {
			// Update readings if Stamp is older than 3 seconds
			Date currentDateTime = new Date(System.currentTimeMillis());
			if (((currentDateTime.getTime() - stamp.getTime())/1000) > 3) {
				getReadings();
			}
			return humidity;
		}
	
}

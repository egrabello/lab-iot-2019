/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  UFRJ / COPPE / PESC
 * PROJECT       :  CPS731 :: IoT Lab
 * FILENAME      :  StatusLEDs.java
 *
 * This file is an implementation work, part of a postgraduate course.
 * Course website (PT-BR):  https://sites.google.com/cos.ufrj.br/lab-iot
 * **********************************************************************
 * 
 * This code was written based on the Pi4J project examples
 * (ControlGpioExample) using the GPIO interface of a Rasp. Pi 3 Model B.

 * #L%
 */
package br.ufrj.cos.iotlab2019.server.resources;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * This class is used to represent the state of an observed object using
 * two leds (red and green):
 *  STATE    RED     GREEN
 *  0        OFF     OFF
 *  1        ON      OFF
 *  2        OFF     ON
 *  4        BLINK   BLINK
 *
 * @author Egberto Rabello
 */
public class StatusLEDs {

    // object state variable
    private int state;
    
    // gpio variables
    final GpioController gpio;
    final GpioPinDigitalOutput RLEDpin;
    final GpioPinDigitalOutput GLEDpin;
    
    // constructor (initialization)
    StatusLEDs(){
        // create gpio controller
        gpio = GpioFactory.getInstance();

        // provision gpio pins #01 and #04 as an output pins and turn them off
        RLEDpin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "Red_LED", PinState.LOW);
        GLEDpin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Green_LED", PinState.LOW);

        // set shutdown state for these pins
        RLEDpin.setShutdownOptions(true, PinState.LOW);
        GLEDpin.setShutdownOptions(true, PinState.LOW);

        // set object state to 0
        state = 0;
    }

    public int getState(){
        return state;
    }
    
    // method to set object state
    public void setState(int newState) {
        // check action based on newState
        if (newState == 0) {
            // State 0 --> both LEDs off
            state = newState;
            RLEDpin.blink(0);
            GLEDpin.blink(0);
            RLEDpin.low();
            GLEDpin.low();
            
        } else if (newState == 1) {
            // State 1 --> Red ON, Green OFF
            state = newState;
            RLEDpin.blink(0);
            GLEDpin.blink(0);
            RLEDpin.high();
            GLEDpin.low();
            
        } else if (newState == 2) {
            // State 2 --> Red OFF, Green ON
            state = newState;
            RLEDpin.blink(0);
            GLEDpin.blink(0);
            RLEDpin.low();
            GLEDpin.high();
            
        } else if (newState == 3) {
            // State 3 --> both LEDs BLINKING
            state = newState;
            RLEDpin.high();
            GLEDpin.high();
            RLEDpin.blink(500);
            GLEDpin.blink(500);
            
        } else {
            // Invalid state entered
            throw new IllegalArgumentException("ERROR: State code out of range! Expected values are 0, 1, 2 or 3.");
        }
    }

    // "close" object to free resources
    public void close(){
        // shutdown GPIO controller
        gpio.shutdown();
        
    }
    
}

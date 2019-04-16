/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  UFRJ / COPPE / PESC
 * PROJECT       :  CPS731 :: IoT Lab
 * FILENAME      :  Exercicio1.java
 *
 * This file is an implementation exercise, part of a postgraduate course.
 * Course website (PT-BR):  https://sites.google.com/cos.ufrj.br/lab-iot
 * **********************************************************************
 * 
 * This code was written based on the Pi4J project examples
 * (ControlGpioExample and ListenGpioExample) using the GPIO interface
 * of a Raspberry Pi 3 Model B.

 * #L%
 */

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

/**
 * This example code demonstrates how to setup a listener
 * and to perform simple state control on the Raspberry Pi.
 *
 * @author Egberto Rabello
 */
public class Exercicio1 {

    public static void main(String args[]) throws InterruptedException {
        System.out.println(" >>> Exercicio 1 ... started.");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput mySwitch = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);

        // set shutdown state for the input pin
        mySwitch.setShutdownOptions(true);
        
        // provision gpio pin #01 as an output pin and set its initial state based on the input pin state
        final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", mySwitch.getState());

        // set shutdown state for the output pin
        pin.setShutdownOptions(true, PinState.LOW);

        // create and register gpio pin listener
        mySwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // set and display pin state on console
                pin.toggle();
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getState());
            }

        });

        // display disclaimer and the initial pin state on console
        System.out.println(" >>> Complete the GPIO circuit and see the listener feedback here in the console.");
        System.out.println(" >>> Press CTRL-C to exit.");
        System.out.println(" --> INITIAL GPIO PIN STATE: " + mySwitch.getState());

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }

    }
}

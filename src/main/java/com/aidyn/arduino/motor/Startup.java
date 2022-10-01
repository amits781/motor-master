package com.aidyn.arduino.motor;

import java.io.IOException;
import java.util.Timer;
import com.aidyn.arduino.motor.utils.ArduinoListner;
import com.fazecast.jSerialComm.SerialPort;

/**
 * Simple application that is part of an tutorial. The tutorial shows how to establish a serial
 * connection between a Java and Arduino program.
 * 
 * @author Michael Schoeffler (www.mschoeffler.de)
 *
 */
public class Startup {

  public static void main(String[] args) throws IOException, InterruptedException {
    SerialPort sp = SerialPort.getCommPort("COM3"); // device name TODO: must be changed
    sp.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
    sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be
                                                                    // written

    if (sp.openPort()) {
      System.out.println("Port is open :)");
      Thread.sleep(4000);
    } else {
      System.out.println("Failed to open port :(");
      return;
    }

    Timer timer = new Timer();
    ArduinoListner listner = new ArduinoListner();
    sp.addDataListener(listner);



    Runtime.getRuntime().addShutdownHook(new Thread(() -> sp.closePort()));

    // if (sp.closePort()) {
    // System.out.println("Port is closed :)");
    // } else {
    // System.out.println("Failed to close port :(");
    // return;
    // }


  }

}

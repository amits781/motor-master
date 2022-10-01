package com.aidyn.arduino.motor.config;

import com.fazecast.jSerialComm.SerialPort;

public class ArduinoConnection {
  private static SerialPort sp = null;

  public static SerialPort getConnection() {
    if (sp != null && sp.openPort()) {
      return sp;
    }
    sp = SerialPort.getCommPort("COM3"); // device name TODO: must be changed
    sp.setComPortParameters(9600, 8, 1, 0); // default connection settings for Arduino
    sp.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0); // block until bytes can be
                                                                    // written
    return sp;
  }
}

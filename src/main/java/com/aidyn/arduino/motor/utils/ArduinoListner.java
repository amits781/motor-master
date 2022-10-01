package com.aidyn.arduino.motor.utils;

import org.springframework.stereotype.Service;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ArduinoListner implements SerialPortDataListener {

  static SerialPort serialPort;

  public static Boolean isInitialized;

  public static String pStatus;

  @Override
  public int getListeningEvents() {
    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
  }

  public static Boolean getIsInit() {
    return isInitialized == null ? false : isInitialized;
  }

  public static String getPStatus() {
    return pStatus == null ? "00" : pStatus;
  }

  @Override
  public void serialEvent(SerialPortEvent event) {
    if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_RECEIVED) {
      serialPort = event.getSerialPort();
      byte[] bufferData = new byte[20];


      int bytesAvailable = serialPort.bytesAvailable();
      if (bytesAvailable <= 0) {
        return;
      }

      int bytesRead = serialPort.readBytes(bufferData, Math.min(bufferData.length, bytesAvailable));
      String dataOutput = new String(bufferData, 0, bytesRead);
      if (!dataOutput.isBlank()) {
        dataOutput = dataOutput.trim();
        log.debug("Arduino Data Received: " + dataOutput);
        if (dataOutput.equals("IP1")) {
          isInitialized = true;
          pStatus = "Pon";
        } else if (dataOutput.equals("IP0")) {
          isInitialized = true;
          pStatus = "Poff";
        } else if (dataOutput.equals("P1")) {
          pStatus = "Pon";
        } else if (dataOutput.equals("P0")) {
          pStatus = "Poff";
        } else if (dataOutput.contains("0")) {
          pStatus = "Poff";
        }
      }
    }
  }

}

package com.aidyn.arduino.motor;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.aidyn.arduino.motor.config.ArduinoConnection;
import com.aidyn.arduino.motor.service.MotorService;
import com.aidyn.arduino.motor.utils.ArduinoListner;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class ArduinoMotorApplication {

  @Autowired
  ArduinoListner listner;

  @Autowired
  MotorService service;

  public static void main(String[] args) {
    SpringApplication.run(ArduinoMotorApplication.class, args);

  }

  @EventListener(ApplicationReadyEvent.class)
  public void doSomethingAfterStartup() throws InterruptedException {
    log.info("Initializing Arduino");
    SerialPort sp = ArduinoConnection.getConnection();
    if (sp.openPort()) {
      Thread.sleep(4000);
      log.debug("Port opened sucessfully");
    } else {
      log.error("Failed to open port");
      return;
    }
    sp.addDataListener(listner);
    try {
      int i = 1;
      do {
        String action = "query:" + i++;
        sp.getOutputStream().write(action.getBytes());
        log.debug("Sent query: " + action);
        log.debug("ArduinoListner.isInitialized: " + ArduinoListner.getIsInit());
        sp.getOutputStream().flush();
        Thread.sleep(5000);
      } while (!ArduinoListner.getIsInit());
    } catch (IOException e) {
      log.error("Failed to initialize data." + e.getLocalizedMessage());
    } finally {
      String action = "query:" + 0;
      try {
        Thread.sleep(4000);
        service.stopMotor(sp);
        sp.getOutputStream().write(action.getBytes());
        sp.getOutputStream().flush();
      } catch (IOException e) {
        log.error("Failed to initialize data." + e.getLocalizedMessage());
      }

    }

  }

}

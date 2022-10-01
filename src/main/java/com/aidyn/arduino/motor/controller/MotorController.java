package com.aidyn.arduino.motor.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.aidyn.arduino.motor.domain.Motor;
import com.aidyn.arduino.motor.service.GoogleSheetsServiceImpl;
import com.aidyn.arduino.motor.service.MotorService;
import com.aidyn.arduino.motor.utils.MotorRabbitMQInterruptSender;
import com.aidyn.arduino.motor.utils.MotorRabbitMQSender;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MotorController {

  @Autowired
  private MotorService motorService;

  @Autowired
  private MotorRabbitMQSender rabbitSender;

  @Autowired
  private MotorRabbitMQInterruptSender rabbitInterruptSender;

  @Autowired
  private GoogleSheetsServiceImpl googleSheetsService;

  @GetMapping(value = "/read")
  public String getSpreadsheetValues() throws IOException, GeneralSecurityException {
    googleSheetsService.getSpreadsheetValues();
    return "OK";
  }

  @GetMapping("/motors")
  public List<Motor> getAllGenres() {
    log.info("All Motors requested");
    return motorService.getAllMotors();
  }

  @GetMapping("/initmotors")
  public Motor initMotor() {
    log.info("Init Motors requested");

    return motorService.initMotor();
  }

  @GetMapping("/start/{seconds}")
  public void startMotor(@PathVariable Integer seconds) {
    log.info("Motor Start requested");
    rabbitSender.send(seconds);
  }

  @GetMapping("/stop")
  public void stopMotor() {
    log.info("Motor Stop requested");
    rabbitInterruptSender.send("stop");
  }
}

package com.aidyn.arduino.motor.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.aidyn.arduino.motor.service.GoogleSheetsServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MotorRabbitGsheetListener {

  @Autowired
  GoogleSheetsServiceImpl service;

  @Autowired
  private MotorRabbitMQSender rabbitSender;

  @Value("${default.motor.id}")
  private Integer defaultId;

  @Value("${minimum.valueable.seconds}")
  private Integer minSec;

  @Value("${current.on.duration}")
  private Integer minOffsetTime;

  @RabbitListener(queues = "motor.gsheet.queue")
  public void listen(String signal) throws IOException, GeneralSecurityException {
    signal = signal.toLowerCase();
    log.info("Gsheet Operation Received : " + signal);
    String splitString[] = signal.split(":");
    if (splitString[0].equals("status")) {
      service.updateMotorValues(signal);
    } else if (splitString[0].equals("error")) {
      service.updateErrorValues(signal);
    }
  }
}

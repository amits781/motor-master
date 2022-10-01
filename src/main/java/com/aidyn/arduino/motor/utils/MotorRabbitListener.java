package com.aidyn.arduino.motor.utils;

import java.io.IOException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.aidyn.arduino.motor.service.MotorService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MotorRabbitListener {

  @Autowired
  MotorService service;

  @RabbitListener(queues = "motor.queue")
  public void listen(Integer time) {
    log.info("Processing Started for : " + time);
    try {
      service.runMotor(time);
    } catch (InterruptedException | IOException e) {
      log.error(e.getMessage());
    }
  }
}

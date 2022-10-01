package com.aidyn.arduino.motor.utils;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.aidyn.arduino.motor.domain.Motor;
import com.aidyn.arduino.motor.service.MotorService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MotorRabbitInterruptListener {

  @Autowired
  MotorService service;

  @Autowired
  private MotorRabbitMQSender rabbitSender;

  @Value("${default.motor.id}")
  private Integer defaultId;

  @Value("${minimum.valueable.seconds}")
  private Integer minSec;

  @Value("${current.on.duration}")
  private Integer minOffsetTime;

  @RabbitListener(queues = "motor.interrupt.queue")
  public void listen(String signal) {
    log.info("Interrupt Received : " + signal);
    if (signal.equals("stop")) {
      service.generateInterrupt();
    } else if (signal.equals("Poff")) {
      service.updatePower(false);
    } else if (signal.equals("Pon")) {
      service.updatePower(true);
      Motor motor = service.getMotorById(defaultId);
      if (motor != null) {
        int sec = motor.getSecondsRem();
        if ((sec > minSec) && (motor.getIsAutostart() != null) && motor.getIsAutostart()) {
          try {
            log.info("Autostart triggered");
            log.info("Waiting for min offset time: " + minOffsetTime);
            Thread.sleep(minOffsetTime * 1000);
            rabbitSender.send(sec);
          } catch (InterruptedException e) {
            log.error(e.getMessage());
          }
        } else {
          log.info("No Autostart required");
        }
      } else {
        log.error("Motor not found: id: " + defaultId);
      }

    } else if (signal.contains("MStop")) {
      String arr[] = signal.split(":");
      Integer i = Integer.parseInt(arr[1]);
      Integer duration = Integer.parseInt(arr[2]);
      log.info("i:" + i + " dur:" + duration);
      service.handlePowerFail(i, duration);
    } else if (signal.contains("Ter")) {
      String arr[] = signal.split(":");
      Integer i = Integer.parseInt(arr[1]);
      Integer duration = Integer.parseInt(arr[2]);
      log.info("i:" + i + " dur:" + duration);
      service.handleUserTermination(i, duration);
    }
  }
}

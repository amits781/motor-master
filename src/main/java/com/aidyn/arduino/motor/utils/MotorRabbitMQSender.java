package com.aidyn.arduino.motor.utils;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MotorRabbitMQSender {

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Value("${motor.rabbitmq.exchange}")
  private String exchange;

  @Value("${motor.rabbitmq.routingkey}")
  private String routingkey;

  public void send(Integer time) {
    amqpTemplate.convertAndSend(exchange, routingkey, time);
    log.info("Sent in Queue for processing = " + time);
  }

  @Scheduled(cron = "0 15 18 * * ?")
  public void scheduleMotor() {
    amqpTemplate.convertAndSend(exchange, routingkey, 2 * 60 * 60);
    log.info("Scheduler started for processing = " + 2 * 60 * 60);
  }
}

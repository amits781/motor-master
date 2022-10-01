package com.aidyn.arduino.motor.utils;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MotorRabbitMQInterruptSender {

  @Autowired
  private AmqpTemplate amqpTemplate;

  @Value("${motor.interrupt.rabbitmq.exchange}")
  private String exchange;

  @Value("${motor.interrupt.rabbitmq.routingkey}")
  private String routingkey;

  public void send(String signal) {
    amqpTemplate.convertAndSend(exchange, routingkey, signal);
    log.info("Sent interrupt in Queue = " + signal);
  }
}

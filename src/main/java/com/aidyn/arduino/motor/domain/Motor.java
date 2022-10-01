package com.aidyn.arduino.motor.domain;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Motor {

  @Id
  @GeneratedValue(generator = "motor-sequence-generator")
  @GenericGenerator(name = "motor-sequence-generator",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {@Parameter(name = "sequence_name", value = "motor_sequence"),
          @Parameter(name = "initial_value", value = "1"),
          @Parameter(name = "increment_size", value = "1")})
  Integer id;

  String status;

  Boolean isAutostart;

  String terReason;

  Integer secondsTot;

  Integer secondsRem;

  Integer totalDurationRan;

  LocalDateTime startTime;

  LocalDateTime stopTime;

}

package com.aidyn.arduino.motor.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.aidyn.arduino.motor.domain.Motor;
import com.aidyn.arduino.motor.repository.MotorRepository;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class MotorDao {
  @Autowired
  MotorRepository repo;

  public List<Motor> getAll() {
    return repo.findAll();
  }

  public Optional<Motor> getById(Integer id) {
    log.info("Querying for motor:" + id);
    return repo.findById(id);
  }

  public Motor save(Motor motor) {
    return repo.save(motor);
  }

}

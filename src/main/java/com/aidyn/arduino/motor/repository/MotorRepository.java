package com.aidyn.arduino.motor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.aidyn.arduino.motor.domain.Motor;

@Repository
public interface MotorRepository extends JpaRepository<Motor, Integer> {

}

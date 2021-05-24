package com.example.logback.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@RestController
@Slf4j
public class LogController {

  @GetMapping("/log/trace")
  public ResponseEntity logTrace(){
    MDC.put("Thread name", Thread.currentThread().getName());
    MDC.put("Thread hash", String.valueOf(Thread.currentThread().hashCode()));
    log.trace("TRACE TEST 입니다.");
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("/log/debug")
  public ResponseEntity logDebug(){
    log.debug("DEBUG TEST 입니다.");
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("/log/info")
  public ResponseEntity logInfo(){
    MDC.put("Thread name", Thread.currentThread().getName());
    MDC.put("Thread hash", String.valueOf(Thread.currentThread().hashCode()));
    log.info("INFO TEST 입니다.");
    return new ResponseEntity(HttpStatus.OK);
  }
}

package com.hatio.taskStream.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {
@GetMapping("/hello")
    public ResponseEntity<String> sampleApi() {
        return new ResponseEntity<>("controller working perfectly", HttpStatus.OK);
    }
}

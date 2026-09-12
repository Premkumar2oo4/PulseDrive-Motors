package com.pulsedrive.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint(
            Principal principal) {

        return ResponseEntity.ok(
                "JWT authentication successful for: "
                        + principal.getName()
        );
    }
}
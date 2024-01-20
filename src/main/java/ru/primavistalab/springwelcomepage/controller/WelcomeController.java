package ru.primavistalab.springwelcomepage.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class WelcomeController {
    @Value("${version}")
    private String version;

    @GetMapping("/")
    public String welcome(String params) {
        return String.format("""
                <h2>Welcome App 2</h2><br>
                <strong>params</strong>=%s<br>
                <strong>version</strong>=%s<br>
                <strong>datetime</strong>=%s<br>
                """, params, version, LocalDateTime.now());
    }
}

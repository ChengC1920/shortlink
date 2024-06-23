package com.thc.shortlink.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/api/shortlink/v1/user/{username}")
    public String getUserByName(@PathVariable("username") String userName) {
        return "Hi," + userName;
    }

}

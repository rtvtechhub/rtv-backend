package com.rtvnewsnetwork.user.controller;

import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.model.UserDto;
import com.rtvnewsnetwork.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping("/user/{id}")
    public User getUserById(String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/user/{id}")
    public User updateUser(@PathVariable String id, UserDto user) {
        return userService.updateUser(id, user);
    }

}

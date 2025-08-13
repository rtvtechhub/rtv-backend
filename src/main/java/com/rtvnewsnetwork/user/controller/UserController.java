package com.rtvnewsnetwork.user.controller;

import com.rtvnewsnetwork.common.service.AuthDetailsHelper;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.model.UserDto;
import com.rtvnewsnetwork.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController implements AuthDetailsHelper {
    @Autowired
    private UserService userService;

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PutMapping("/user")
    public User updateUser( @Valid @RequestBody UserDto user) {
        return userService.updateUser(getUserId(), user);
    }

    @GetMapping("/user")
    public User getUserByToken(){
        return getUser();
    }
}

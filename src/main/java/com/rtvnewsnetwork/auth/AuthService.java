package com.rtvnewsnetwork.auth;

import com.rtvnewsnetwork.user.model.User;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<User> login(LoginRequest loginRequest) throws BadRequestException;
}

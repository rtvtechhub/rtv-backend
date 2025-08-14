package com.rtvnewsnetwork.auth;

import com.rtvnewsnetwork.config.jwt.JwtTokenUtils;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.service.UserService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    public AuthServiceImpl(UserService userService, JwtTokenUtils jwtTokenUtils, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenUtils = jwtTokenUtils;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<User> login(LoginRequest loginRequest) {
        User userDetails = userService.findByPhoneNumber(loginRequest.getPhoneNumber());
        if (userDetails == null) {
            throw new UsernameNotFoundException("User not found with username: " + loginRequest.getPhoneNumber());
        }

        if (passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            String jwtToken = jwtTokenUtils.generateToken(userDetails);
            HttpHeaders headers = new HttpHeaders();
            headers.set("jwt-token", jwtToken);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(userDetails);
        } else {
            throw new BadCredentialsException("Invalid Credentials");
        }

    }
}

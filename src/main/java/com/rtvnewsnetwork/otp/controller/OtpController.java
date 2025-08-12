package com.rtvnewsnetwork.otp.controller;

import com.rtvnewsnetwork.config.jwt.JwtTokenUtils;
import com.rtvnewsnetwork.otp.service.OtpService;
import com.rtvnewsnetwork.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class OtpController {

    private final OtpService otpService;
    private final JwtTokenUtils jwtUtils;

    @Autowired
    public OtpController(OtpService otpService, JwtTokenUtils jwtUtils) {
        this.otpService = otpService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/sendOtp")
    public ResponseEntity<String> sendOtp(
            @RequestParam String phoneNumber,
            @RequestParam(required = false, defaultValue = "") String apphash
    ) {
        try {
            otpService.sendOtp(phoneNumber, apphash);
            return ResponseEntity.status(HttpStatus.OK).body("Code Sent");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<User> verifyOtp(
            @RequestParam String phoneNumber,
            @RequestParam String otp
    ) {
        try {
            User user = otpService.verifyOtp(phoneNumber, otp);
            String jwtToken = jwtUtils.generateToken(user);
            String refreshToken = jwtUtils.generateRefreshToken(user);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", jwtToken);
            headers.add("RefreshToken", refreshToken);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .body(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to verify OTP!");
        }
    }
}


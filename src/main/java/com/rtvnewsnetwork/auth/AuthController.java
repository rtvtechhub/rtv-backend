package com.rtvnewsnetwork.auth;

import com.rtvnewsnetwork.config.jwt.JwtTokenUtils;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final JwtTokenUtils jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AuthController(JwtTokenUtils jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("api/auth/login")
    public UserDetails getUserDetails(HttpServletResponse response) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Generate JWT and refresh token
        String token = jwtTokenProvider.generateToken((User) userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken((User) userDetails);

        // Set tokens in headers
        response.setHeader("Authorization", token);
        response.setHeader("RefreshToken", refreshToken);

        return userDetails;
    }

    @PostMapping("api/auth/refresh")
    public UserDetails refreshToken(@RequestBody RefreshToken refreshTokenRequest, HttpServletResponse response) {
        // Parse the refresh token to extract claims
        String userId = jwtTokenProvider
                .parseRefreshToken(refreshTokenRequest.getRefreshToken())
                .getBody()
                .getSubject();

        // Get user by ID
        UserDetails userDetails = userService.getUserById(userId);

        // Generate new access token
        String accessToken = jwtTokenProvider.generateToken((User) userDetails);

        response.setHeader("Authorization", accessToken);
        response.setHeader("RefreshToken", refreshTokenRequest.getRefreshToken());

        return userDetails;
    }
}

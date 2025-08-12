package com.rtvnewsnetwork.otp.service;


import com.rtvnewsnetwork.common.exception.ExternalServiceException;
import com.rtvnewsnetwork.otp.model.OtpException;
import com.rtvnewsnetwork.otp.model.OtpResponse;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OtpServiceImpl implements OtpService {

    private final UserService userService;
    private final RestTemplate restTemplate;
    @Value("${rtp.2factor.baseurl}")
    private String otpBaseUrl;
    public OtpServiceImpl(
            UserService userService,
            RestTemplate restTemplate
    ) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @Override
    public OtpResponse sendOtp(String phoneNumber, String apphash) throws OtpException {
        String otpUrl = otpBaseUrl + "+91" + phoneNumber+"/AUTOGEN2/OTP1"; // Consider appending apphash if API requires it

        try {
            ResponseEntity<OtpResponse> responseEntity =
                    restTemplate.getForEntity(otpUrl, OtpResponse.class);

            OtpResponse response = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK && response != null) {
                if ("Success".equalsIgnoreCase(response.getStatus())) {
                    return response;
                } else {
                    throw new OtpException(response.getDetails() != null
                            ? response.getDetails()
                            : "OTP sending failed");
                }
            } else {
                throw new OtpException("Invalid response from OTP service");
            }

        } catch (Exception e) {
            throw new ExternalServiceException("Failed to send OTP: " + e.getMessage(), e);
        }
    }




    @Override
    public User verifyOtp(String phoneNumber, String otp) throws OtpException {
        String otpUrl=otpBaseUrl+"VERIFY3/91"+phoneNumber+"/"+otp;
        try {
            ResponseEntity<OtpResponse> responseEntity =
                    restTemplate.getForEntity(otpUrl, OtpResponse.class);

            OtpResponse response = responseEntity.getBody();

            if (responseEntity.getStatusCode() == HttpStatus.OK && response != null) {
                if ("Success".equalsIgnoreCase(response.getStatus())) {
                    return userService.findByUsernameElseCreate("+91"+phoneNumber);
                } else {
                    throw new OtpException(response.getDetails() != null
                            ? response.getDetails()
                            : "OTP sending failed");
                }
            } else {
                throw new OtpException("Invalid response from OTP service");
            }

        } catch (Exception e) {
            throw new ExternalServiceException("Failed to send OTP: " + e.getMessage(), e);
        }
    }

}
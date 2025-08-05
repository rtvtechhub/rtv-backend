package com.rtvnewsnetwork.otp.service;


import com.otpless.authsdk.OTPAuth;
import com.otpless.authsdk.OTPResponse;
import com.otpless.authsdk.OTPVerificationResponse;
import com.rtvnewsnetwork.common.exception.ResourceNotFoundException;
import com.rtvnewsnetwork.otp.model.Fast2OtpModel;
import com.rtvnewsnetwork.otp.model.OtpException;
import com.rtvnewsnetwork.otp.model.OtpRequest;
import com.rtvnewsnetwork.otp.repository.Fast2OtpRequestRepository;
import com.rtvnewsnetwork.otp.repository.OtpRequestRepository;
import com.rtvnewsnetwork.user.model.User;
import com.rtvnewsnetwork.user.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class OtpServiceImpl implements OtpService {

    private final UserService userService;
    private final OtpRequestRepository otpRequestRepository;
    private final Fast2OtpRequestRepository otpRepository;
    private final Fast2SmsService fast2SmsService;
    private final OTPAuth otplessSdk;

    public OtpServiceImpl(
            UserService userService,
            OtpRequestRepository otpRequestRepository,
            Fast2OtpRequestRepository otpRepository,
            Fast2SmsService fast2SmsService,
            @Value("${interkashi.otp.oppless.clientid}") String otpLessClientId,
            @Value("${interkashi.otp.oppless.clientsecret}") String otpLessClientSecret
    ) {
        this.userService = userService;
        this.otpRequestRepository = otpRequestRepository;
        this.otpRepository = otpRepository;
        this.fast2SmsService = fast2SmsService;
        this.otplessSdk = new OTPAuth(otpLessClientId, otpLessClientSecret);
    }

    @Override
    public void sendOtp(String phoneNumber, String apphash) throws OtpException {
        Optional<OtpRequest> otpRequestOpt = otpRequestRepository.findByPhoneNumberAndActive(phoneNumber, true);

        if (otpRequestOpt.isEmpty()) {
            OtpRequest otpRequest = OtpRequest.builder()
                    .id(null)
                    .phoneNumber(phoneNumber)
                    .orderId(UUID.randomUUID().toString())
                    .appHash(apphash.isEmpty() ? null : apphash)
                    .build();
            otpRequest = otpRequestRepository.save(otpRequest);
            OTPResponse otpResponse = otplessSdk.sendOTP(
                    otpRequest.getOrderId(),
                    "91" + otpRequest.getPhoneNumber(),
                    null,
                    otpRequest.getAppHash(),
                    300,
                    6,
                    "SMS"
            );
            if (otpResponse.isSuccess()) {
                System.out.println("OTP sent. orderId=> " + otpResponse.getOrderId());
            } else {
                onOtpRequestFailure(otpRequest, otpResponse);
                System.out.println("OTP send to failed due to " + otpResponse.getErrorMessage());
            }
        } else {
            OtpRequest otpRequest = otpRequestOpt.get();
            if (otpRequest.getCount() < 3) {
                OTPResponse otpResponse = otplessSdk.resendOTP(otpRequest.getOrderId());
                if (otpResponse.isSuccess()) {
                    otpRequest.incrementCount();
                    save(otpRequest);
                    System.out.println("OTP sent. orderId=> " + otpResponse.getOrderId());
                } else {
                    onOtpRequestFailure(otpRequest, otpResponse);
                    System.out.println("OTP send to failed due to " + otpResponse.getErrorMessage());
                }
            } else {
                otpRequest.setActive(false);
                save(otpRequest);
                sendOtp(phoneNumber, apphash);
            }
        }
    }

    private void onOtpRequestFailure(OtpRequest otpRequest, OTPResponse otpResponse) throws OtpException {
        otpRequest.setMessage(otpResponse.getErrorMessage());
        otpRequest.setActive(false);
        save(otpRequest);
        throw new OtpException(otpResponse.getErrorMessage());
    }

    @Override
    public void resendOtp(String phoneNumber) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public User verifyOtp(String phoneNumber, String otp) throws OtpException {
        Optional<OtpRequest> otpRequestOpt = otpRequestRepository.findByPhoneNumberAndActive(phoneNumber, true);

        if (otpRequestOpt.isPresent()) {
            OtpRequest otpRequest = otpRequestOpt.get();
            OTPVerificationResponse response = otplessSdk.verifyOTP(
                    otpRequest.getOrderId(),
                    otp,
                    "91" + phoneNumber,
                    null
            );

            if (response.getIsOTPVerified()) {
                otpRequest.setActive(false);
                save(otpRequest);
                return userService.findByUsernameElseCreate("+91" + phoneNumber);
            } else {
                if (otpRequest.getVerifyCount() > 2) {
                    otpRequest.setActive(false);
                } else {
                    otpRequest.setVerifyCount(otpRequest.getVerifyCount() + 1);
                }
                save(otpRequest);
                System.out.println(response.getErrorMessage());
                throw new OtpException(response.getErrorMessage());
            }
        } else {
            throw new ResourceNotFoundException("OTP request not found!");
        }
    }

    @Override
    public OtpRequest save(OtpRequest otpRequest) {
        return otpRequestRepository.save(otpRequest);
    }

    @Override
    public void sendOtpViaFast2Sms(String phoneNumber, String apphash) throws OtpException {
        Instant now = Instant.now();
        Fast2OtpModel existingOtp = otpRepository.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber);

        if (existingOtp != null && Duration.between(existingOtp.getLastSentAt(), now).getSeconds() < 60) {
            throw new OtpException("OTP can not be sent within 1 minute");
        }

        String otp = generateOtp();
        Fast2OtpModel otpModel = Fast2OtpModel.builder()
                .phoneNumber(phoneNumber)
                .otp(otp)
                .createdAt(now)
                .expiresAt(now.plusSeconds(300))
                .lastSentAt(now)
                .build();

        otpRepository.save(otpModel);
        fast2SmsService.sendOtpSms(phoneNumber, otp);
    }

    private String generateOtp() {
        return String.valueOf(100000 + (int)(Math.random() * 900000));
    }

    @Override
    public User verifyOtpViaFast2Sms(String phoneNumber, String inputOtp) throws OtpException {
        Fast2OtpModel existingOtp = otpRepository.findTopByPhoneNumberOrderByCreatedAtDesc(phoneNumber);

        if (existingOtp == null) {
            throw new OtpException("OTP not found");
        }

        Instant now = Instant.now();

        if (existingOtp.isUsed()) {
            throw new OtpException("Verify with valid OTP");
        } else if (now.isAfter(existingOtp.getExpiresAt())) {
            throw new OtpException("OTP has expired");
        } else if (existingOtp.getFailedAttempts() >= 3) {
            throw new OtpException("Maximum OTP attempts exceeded");
        } else if (existingOtp.getOtp().equals(inputOtp)) {
            existingOtp.setUsed(true);
            otpRepository.save(existingOtp);
            return userService.findByUsernameElseCreate("+91" + phoneNumber);
        } else {
            existingOtp.setFailedAttempts(existingOtp.getFailedAttempts() + 1);
            otpRepository.save(existingOtp);
            if (existingOtp.getFailedAttempts() >= 3) {
                throw new OtpException("You have exceeded the maximum attempts");
            } else {
                throw new OtpException("Incorrect OTP");
            }
        }
    }
}
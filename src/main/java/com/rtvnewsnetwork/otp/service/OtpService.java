package com.rtvnewsnetwork.otp.service;

import com.rtvnewsnetwork.otp.model.OtpException;
import com.rtvnewsnetwork.otp.model.OtpRequest;
import com.rtvnewsnetwork.user.model.User;

public interface OtpService {


    void sendOtp(String phoneNumber, String apphash) throws OtpException;

    void resendOtp(String phoneNumber);

    User verifyOtp(String phoneNumber, String otp)throws OtpException;

    OtpRequest save(OtpRequest otpRequest);

    void sendOtpViaFast2Sms(String phoneNumber, String apphash) throws OtpException;

    User verifyOtpViaFast2Sms(String phoneNumber, String inputOtp) throws OtpException;
}

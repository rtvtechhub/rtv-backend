package com.rtvnewsnetwork.otp.service;

import com.rtvnewsnetwork.otp.model.OtpException;
import com.rtvnewsnetwork.otp.model.OtpResponse;
import com.rtvnewsnetwork.user.model.User;

public interface OtpService {


    OtpResponse sendOtp(String phoneNumber, String apphash) throws OtpException;

    User verifyOtp(String phoneNumber, String otp)throws OtpException;
}

package com.rtvnewsnetwork.otp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {

    @JsonProperty("Status")
    private String status;

    @JsonProperty("Details")
    private String details;

    @JsonProperty("OTP")
    private String otp;
}

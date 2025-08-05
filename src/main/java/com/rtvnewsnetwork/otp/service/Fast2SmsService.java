package com.rtvnewsnetwork.otp.service;


import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class Fast2SmsService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "https://www.fast2sms.com/dev/bulkV2";
    private final String apiKey = "xOonXrhBD4GWisqt3EeYKNvgCSfu6cyw295Mz8IHV7aP1Fdl0jnwvMHEUJcVb7PASN4xm9d8YOGTzueo";

    public String sendOtpSms(String phoneNumber, String otp) {
        String message = "Dear user, your OTP for InterKashi login is " + otp +
                ". Do not share it with anyone. - InterKashi";

        String uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("authorization", apiKey)
                .queryParam("message", message)
                .queryParam("language", "english")
                .queryParam("route", "q")
                .queryParam("numbers", phoneNumber)
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return "OTP sent successfully to " + phoneNumber;
            } else {
                return "Failed to send OTP: " + response.getStatusCode();
            }
        } catch (Exception ex) {
            return "Error while sending OTP: " + ex.getLocalizedMessage();
        }
    }
}

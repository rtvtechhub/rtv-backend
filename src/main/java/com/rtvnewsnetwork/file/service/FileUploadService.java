package com.rtvnewsnetwork.file.service;


import com.rtvnewsnetwork.config.jwt.JwtFileTokenUtils;

import com.rtvnewsnetwork.config.model.UploadedFile;
import com.rtvnewsnetwork.file.model.VideoAndThumbnailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    @Autowired
    private  RestTemplate restTemplate;
    @Autowired
    private  JwtFileTokenUtils jwtFileTokenUtils; // Inject JWT generator

    @Value("${rtv.file.service.url}")
    private String fileServiceUrl;

    public UploadedFile uploadFile(MultipartFile file) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> requestEntity = buildMultipartRequest(file);
        ResponseEntity<UploadedFile> response = restTemplate.exchange(
                fileServiceUrl + "/uploadFile",
                HttpMethod.POST,
                requestEntity,
                UploadedFile.class
        );
        return response.getBody();
    }

    public VideoAndThumbnailResponse uploadVideoAndThumbnail(
            MultipartFile file,
            String videoPath,
            String thumbnailPath
    ) throws Exception {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
        body.add("videoPath", videoPath);
        body.add("thumbnailPath", thumbnailPath);

        HttpHeaders headers = getAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<VideoAndThumbnailResponse> response = restTemplate.exchange(
                fileServiceUrl + "/videoAndThumbnailUpload",
                HttpMethod.POST,
                requestEntity,
                VideoAndThumbnailResponse.class
        );
        return response.getBody();
    }

    private HttpEntity<MultiValueMap<String, Object>> buildMultipartRequest(MultipartFile file) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpHeaders headers = getAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(body, headers);
    }

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String jwtToken = jwtFileTokenUtils.generateToken(); // Generate new token each request
        headers.set("Authorization", "Bearer " + jwtToken);
        return headers;
    }
}

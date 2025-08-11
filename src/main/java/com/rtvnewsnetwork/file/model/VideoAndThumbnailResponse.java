package com.rtvnewsnetwork.file.model;

import com.rtvnewsnetwork.config.model.UploadedFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoAndThumbnailResponse {
    private UploadedFile video;
    private UploadedFile thumbnail;
}
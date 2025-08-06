package com.rtvnewsnetwork.user.model;

import com.rtvnewsnetwork.config.model.UploadedFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoV2 {
    private String name;
    private String email;
    private UploadedFile.RelativePath profileImage;
}

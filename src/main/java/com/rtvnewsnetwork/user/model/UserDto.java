package com.rtvnewsnetwork.user.model;

import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotBlank(message = "Name is required")
    private String name;
    private String email;
    private Gender gender;
    private Integer age;
    private RelativePath profileImage;
}

package com.rtvnewsnetwork.user.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class UserDto {
    @NonNull
    private String name;
    private String email;
    private Gender gender;
    private Integer age;
}

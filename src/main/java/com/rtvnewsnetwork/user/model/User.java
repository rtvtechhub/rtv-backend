package com.rtvnewsnetwork.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rtvnewsnetwork.config.model.UploadedFile.RelativePath;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    private String id;
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private Gender gender;
    private Integer age;
    private RelativePath profileImage;
    private List<String> authorities;
    private UserWallet userWallet;
    @JsonIgnore
    private Map<String, String> fcmToken = new HashMap<>();

    @JsonIgnore

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(item -> new SimpleGrantedAuthority(item.toString()))
                .collect(Collectors.toList());
    }


    @JsonIgnore
    @Override
    public String getPassword() {
        return this.password;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.phoneNumber;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}

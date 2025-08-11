package com.rtvnewsnetwork.common.service;

import com.rtvnewsnetwork.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public interface AuthDetailsHelper {

    default Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    default User getUser() {
        Authentication auth = getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        }
        return null;
    }

    default String getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }
}

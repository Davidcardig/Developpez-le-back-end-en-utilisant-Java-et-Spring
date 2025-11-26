package com.chatop.mappers;

import com.chatop.dtos.UserResponse;
import com.chatop.models.User;

import java.time.format.DateTimeFormatter;

public class UserMapper {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE_TIME;

    public static User toEntity(UserResponse req) {
        if (req == null) return null;
        User user = new User();
        user.setName(req.getName());
        user.setEmail(req.getEmail());
        return user;
    }

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        UserResponse r = new UserResponse();
        r.setId(user.getId());
        r.setName(user.getName());
        r.setEmail(user.getEmail());
        r.setCreatedAt(user.getCreatedAt() != null ? user.getCreatedAt().format(ISO) : null);
        r.setUpdatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().format(ISO) : null);
        return r;
    }
}


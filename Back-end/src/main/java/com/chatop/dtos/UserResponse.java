package com.chatop.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private String createdAt;
    private String updatedAt;

}


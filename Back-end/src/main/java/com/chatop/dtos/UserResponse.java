package com.chatop.dtos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
  private String password;
    private String createdAt;
    private String updatedAt;


}


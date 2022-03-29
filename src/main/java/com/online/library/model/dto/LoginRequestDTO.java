package com.online.library.model.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequestDTO {

    @NotBlank
    @Size(max = 50)
    @Email
    private String username;

    @NotBlank
    private String password;
}

package com.shuyan.sonicsocial.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserRegisterDTO implements Serializable {

    @NotBlank(message = "username can't be null")
    private String username;

    @NotBlank(message = "password can't be null")
    private String password;

    @NotBlank(message = "email can't be null")
    @Email(message = "email format is wrong")
    private String email;

    @NotBlank(message = "Captcha code can't be null")
    private String captchaCode;

    @JsonIgnore
    private String salt = UUID.randomUUID().toString().replaceAll("-", "");

}

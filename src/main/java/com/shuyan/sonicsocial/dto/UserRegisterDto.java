package com.shuyan.sonicsocial.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRegisterDto {

    @NotBlank(message = "username can't be null")
    private String username;

    @NotBlank(message = "password can't be null")
    private String password;

    @NotBlank(message = "email can't be null")
    @Email(message = "email format is wrong")
    private String email;

    @NotBlank(message = "reCAPTCHA response is required")
    private String recaptchaResponse;

}

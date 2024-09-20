package com.shuyan.sonicsocial.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserLoginDto {
    @NotBlank(message = "username can't be null")
    private String username;

    @NotBlank(message = "password can't be null")
    private String password;

    @NotBlank(message = "reCAPTCHA response is required")
    private String recaptchaResponse;
}

package com.shuyan.sonicsocial.controller;


import com.shuyan.sonicsocial.dto.UserLoginDto;
import com.shuyan.sonicsocial.dto.UserRegisterDto;
import com.shuyan.sonicsocial.result.Result;
import com.shuyan.sonicsocial.result.ResultCode;
import com.shuyan.sonicsocial.service.CaptchaService;
import com.shuyan.sonicsocial.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user")
@Slf4j
@Api(tags = "user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private CaptchaService captchaService;

    @PostMapping("/register")
    @ApiOperation("userRegister")
    public Result register(@RequestBody @Validated UserRegisterDto userRegisterDto){
        String recaptchaResponse = userRegisterDto.getRecaptchaResponse();
        boolean isCaptchaValid = captchaService.verifyCaptcha(recaptchaResponse);
        if (!isCaptchaValid) {
            return Result.error(ResultCode.VALIDATION_ERROR,"Invalid reCAPTCHA. Please try again.");
        }
        return userService.register(userRegisterDto);
    }

    @PostMapping("/login")
    @ApiOperation("userLogin")
    public Result login(@RequestBody @Validated UserLoginDto userloginDto){
        String recaptchaResponse = userloginDto.getRecaptchaResponse();
        boolean isCaptchaValid = captchaService.verifyCaptcha(recaptchaResponse);
        if (!isCaptchaValid) {
            return Result.error(ResultCode.VALIDATION_ERROR,"Invalid reCAPTCHA. Please try again.");
        }
       return userService.login(userloginDto);
    }


}

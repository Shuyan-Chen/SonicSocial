package com.shuyan.sonicsocial.service;

public interface CaptchaService {
    boolean verifyCaptcha(String recaptchaResponse);

}

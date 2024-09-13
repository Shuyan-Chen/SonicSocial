package com.shuyan.sonicsocial.service;


import com.shuyan.Captcha;

import java.awt.image.BufferedImage;


public interface CaptchaService {

    BufferedImage getCaptcha(String uuId);

    boolean validate(Captcha captcha) ;

}

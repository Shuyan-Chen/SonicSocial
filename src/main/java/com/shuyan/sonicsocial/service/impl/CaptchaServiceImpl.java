package com.shuyan.sonicsocial.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shuyan.Captcha;
import com.shuyan.exception.BaseException;
import com.shuyan.service.CaptchaService;
import com.shuyan.utils.CaptchaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;


@Service
public class CaptchaServiceImpl implements CaptchaService {


    @Autowired
    private CaptchaProducer captchaProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();


    public BufferedImage getCaptcha(String uuId) {
        String code = captchaProducer.createText(6);
        Captcha captcha = new Captcha();
        captcha.setUuid(uuId);
        captcha.setCode(code);
        captcha.setExpireTime(DateUtil.convertToDate(LocalDateTime.now().plusMinutes(5)));
        try {
            String captchaJson = objectMapper.writeValueAsString(captcha);
            redisTemplate.opsForValue().set("captcha:" + uuId, captchaJson, 5, TimeUnit.MINUTES);
        }catch (Exception e){
            e.printStackTrace();
        }
        return captchaProducer.createImage(code);
    }


    public boolean validate(Captcha captcha) {

        String captchaJson = (String) redisTemplate.opsForValue().get("captcha:" + captcha.getUuid());
        if (captchaJson == null) {
            throw new BaseException("验证码已过期或不存在");
        }

        try {
            Captcha storedCaptcha = objectMapper.readValue(captchaJson, Captcha.class);
            if (!storedCaptcha.getCode().equals(captcha.getCode())) {
                throw new BaseException("验证码错误");
            }
            if (storedCaptcha.getExpireTime().getTime() <= System.currentTimeMillis()) {
                throw new BaseException("验证码已过期");
            }
            redisTemplate.delete("captcha:" + captcha.getUuid());
        } catch (Exception e) {
            throw new BaseException("验证码验证失败" + e.getMessage());
        }
        return true;
     }


}


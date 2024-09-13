package com.shuyan.sonicsocial.utils;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

@Component
public class CaptchaProducer {

    private static final String CHAR_STRING =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private Random random = new Random();

    public String createText(int length){
        StringBuilder captchaText = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_STRING.length());
            captchaText.append(CHAR_STRING.charAt(index));
        }
        return captchaText.toString();
    }

    public BufferedImage createImage(String code) {
        int width = 200;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        g.drawString(code, 20, 50);
        g.dispose();
        return image;
    }

}

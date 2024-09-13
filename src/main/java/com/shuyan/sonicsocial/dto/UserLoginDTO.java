package com.shuyan.sonicsocial.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements Serializable {

    private String username;
    private String password;

}

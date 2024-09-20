package com.shuyan.sonicsocial.result;

import lombok.Data;

import java.io.Serializable;


@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = ResultCode.SUCCESS;
        result.msg = "Success";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = ResultCode.SUCCESS;
        result.msg = "Success";
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(int code, String msg) {
        Result result = new Result();
        result.code = code;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(int code, String msg, T errorDetails) {
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = msg;
        result.data = errorDetails;
        return result;
    }



}

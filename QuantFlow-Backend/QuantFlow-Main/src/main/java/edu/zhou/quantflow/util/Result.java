package edu.zhou.quantflow.util;

import lombok.Data;

/**
 * @Author Righter
 * @Description
 * @Date since 3/6/2025
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    // Constructors, getters and setters

    public Result() {}

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }


    // Getters and Setters...

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    public static  Result<Void> success(){
        return new Result<>(200, "操作成功",null);
    }

    public static Result<Void> error(String message) {
        return new Result<>(500, message, null);
    }
}

package edu.zhou.quantflow.exceptionhandler;


import edu.zhou.quantflow.util.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @Author Righter
 * @Description 处理全局的错误
 * @Date since 3/8/2025
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler  {

    @ExceptionHandler(Exception.class)
    public Result<?> handleGlobalException(Exception e){
        log.error(e);
        return Result.error("Internal Server Error :"+e.getMessage());
    }

}

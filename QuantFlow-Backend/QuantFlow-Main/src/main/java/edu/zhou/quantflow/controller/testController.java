package edu.zhou.quantflow.controller;

import edu.zhou.quantflow.util.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author Righter
 * @Description
 * @Date since 3/15/2025
 */
@RestController
@RequestMapping("/test")
public class testController {

    @RequestMapping("/test1")
    public Result<?> test(){
        return Result.success("This is test1");
    }
    @RequestMapping("/test2")
    public Result<String> test2(){
        return Result.success("This is test2");
    }

    @RequestMapping("/test3")
    public Result<String> test3(){
        return Result.success("This is test3");
    }

}

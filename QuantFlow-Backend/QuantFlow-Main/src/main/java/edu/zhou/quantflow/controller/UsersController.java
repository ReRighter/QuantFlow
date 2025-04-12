package edu.zhou.quantflow.controller;


import edu.zhou.quantflow.entity.Users;
import edu.zhou.quantflow.service.IUsersService;
import edu.zhou.quantflow.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-05
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    private IUsersService usersService;

    @Autowired
    public void setUsersService(IUsersService usersService) {
        this.usersService = usersService;
    }



    @PostMapping("/list")
    public Result<?> list() {
        return Result.success(usersService.list());
    }
    @PostMapping("/register")
    public Result<?> register(@RequestBody Users user) {
        boolean success =usersService.register(user);
        System.out.println(user.toString());
        if(success){
            return Result.success();
        }else return Result.error("用户名已被注册");
    }




}

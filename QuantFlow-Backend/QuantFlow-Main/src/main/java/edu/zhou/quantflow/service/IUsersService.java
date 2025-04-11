package edu.zhou.quantflow.service;

import edu.zhou.quantflow.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-05
 */
public interface IUsersService extends IService<Users> {
    /*//注册

    //登录
    Users login(String username,String password);*/
    boolean register(Users user);

}

package edu.zhou.quantflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.zhou.quantflow.entity.Users;
import edu.zhou.quantflow.mapper.UsersMapper;
import edu.zhou.quantflow.service.IUsersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.zhou.quantflow.util.PasswordUtil;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-05
 */
@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements IUsersService {

    @Override
    public boolean register(Users user) {
        Users newUser = getOne(new LambdaQueryWrapper<Users>().eq(Users::getUsername, user.getUsername()));
        if (newUser == null) {
            newUser = new Users();
            newUser.setUsername(user.getUsername());
            newUser.setPassword(PasswordUtil.encode(user.getPassword()));
            newUser.setEmail(user.getEmail());
            newUser.setStatus((short)1);
            save(newUser);
            return true;
        }

        return false;
        /*UserDetails userDetails = User
                .withUsername(user.getUsername())
                .password(PasswordUtil.encode(user.getPassword())) // 密码加密
                .authorities("ROLE_USER")
                .build();
        dbUserDetailsManager.createUser(userDetails);
        return true;*/
    }

    /*@Override
    public Users login(String username,String password) {
        *//*Users existingUser = getOne(new LambdaQueryWrapper<Users>().eq(Users::getUsername, username));
        if (existingUser != null && PasswordUtil.matches(PasswordUtil.encode(password), existingUser.getPasswordHash()) && existingUser.getStatus() == 1) {
            return existingUser;
        }
        return null;*//*
        return null;
    }*/
}

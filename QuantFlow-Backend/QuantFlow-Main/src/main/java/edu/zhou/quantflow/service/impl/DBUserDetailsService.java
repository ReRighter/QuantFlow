package edu.zhou.quantflow.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.zhou.quantflow.entity.Users;
import edu.zhou.quantflow.mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import edu.zhou.quantflow.entity.MyUserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * @Author Righter
 * @Description 该类继承了UserDetailsService,
 * @Date since 3/7/2025
 */
@Service
public class DBUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private UsersMapper usersMapper;
    @Autowired
    public void setUsersMapper(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersMapper.selectOne(new LambdaQueryWrapper<Users>().eq(Users::getUsername, username));
        if (user != null) {
            Collection<SimpleGrantedAuthority> authorities = new HashSet<>();

            if(username.equals("admin")) authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); //管理员特权
            authorities.add(new SimpleGrantedAuthority("TEST"));
            return new MyUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    user.getEmail(),
                    user.getStatus(),
                    authorities
            );
        } else {
            throw new UsernameNotFoundException("用户 %s 不存在".formatted(username));
        }
    }
}

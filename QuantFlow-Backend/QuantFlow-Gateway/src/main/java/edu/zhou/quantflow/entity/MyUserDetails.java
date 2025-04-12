package edu.zhou.quantflow.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @Author Righter
 * @Description 自定义用户信息, 额外包含了id, email, 用于控制器返回前端
 * @Date since 3/8/2025
 */
@AllArgsConstructor
public class MyUserDetails implements UserDetails {
    @Getter
    private int id ;
    private String username;
    private String password;
    @Getter
    private String email;
    @Getter
    private short status;
    private Collection<SimpleGrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

}

package edu.zhou.quantflow.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @Author Righter
 * @Description
 * @Date since 3/5/2025
 */
@Getter
@Setter
@ToString
public class UsersDto {
    private Integer id;

    private String username;

    private String email;

    private String role;

    public UsersDto(String role, String email, String username, Integer id) {
        this.role = role;
        this.email = email;
        this.username = username;
        this.id = id;
    }

    public UsersDto() {}
}

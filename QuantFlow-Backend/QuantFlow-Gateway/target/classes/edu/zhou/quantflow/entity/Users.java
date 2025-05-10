package edu.zhou.quantflow.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * <p>
 * 
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-05
 */
@Getter
@Setter
@Table("users")
public class Users implements Serializable {

    @Id
    private Integer id;

    private String username;

    private String email;

    private String password;

    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    /**
     * 账户状态（0-禁用 1-正常）
     */
    private Short status;
}

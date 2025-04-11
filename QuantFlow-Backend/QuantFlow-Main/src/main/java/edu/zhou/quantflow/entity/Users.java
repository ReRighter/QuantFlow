package edu.zhou.quantflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
@ToString
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     */
    @TableId(value = "id", type = IdType.AUTO)
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

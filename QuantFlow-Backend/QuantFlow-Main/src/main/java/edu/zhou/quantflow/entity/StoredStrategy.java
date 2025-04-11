package edu.zhou.quantflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
/**
 * <p>
 * 预存储策略
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-31
 */
@Getter
@Setter
@ToString
@TableName("stored_strategy")
public class StoredStrategy implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    /**
     * 策略代码
     */
    private String code;
}

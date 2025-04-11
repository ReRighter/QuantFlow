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
 * @since 2025-03-13
 */
@Getter
@Setter
@ToString
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String minioPath;

    /**
     * 未绑定和已绑定两种状态
     */
    private String status;

    private LocalDateTime createAt;
}

package edu.zhou.quantflow.dto;

import edu.zhou.quantflow.entity.Course;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @Author Righter
 * @Description
 * @Date since 3/23/2025
 */
@Getter
@Setter
public class CourseInfoDto {
    private Course course;
    private String url;
}

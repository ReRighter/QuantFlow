package edu.zhou.quantflow.dto;

import edu.zhou.quantflow.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @Author Righter
 * @Description
 * @Date since 3/16/2025
 */
@Getter
@Setter
@AllArgsConstructor
public class CourseTreeDto {
    private Course parent_course;
    private List<Course> lessons;
    private String cover;
}

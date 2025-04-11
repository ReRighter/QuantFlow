package edu.zhou.quantflow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.zhou.quantflow.dto.CourseTreeDto;
import edu.zhou.quantflow.entity.Course;
import edu.zhou.quantflow.service.ICourseService;
import edu.zhou.quantflow.util.PasswordUtil;
import edu.zhou.quantflow.util.ResourceUrlUtil;
import edu.zhou.quantflow.util.Result;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Righter
 * @Description
 * @Date since 3/23/2025
 */
@RestController
@RequestMapping("/course")
public class CourseController {
    private ICourseService courseService;
    private ResourceUrlUtil resourceUrlUtil;
    @Autowired
    public CourseController(ICourseService courseService,ResourceUrlUtil resourceUrlUtil){
        this.courseService = courseService;
        this.resourceUrlUtil = resourceUrlUtil;
    }

    @GetMapping("/list")
    public Result<List<CourseTreeDto>> courseList(){
        List<Course> list = courseService.list(new LambdaQueryWrapper<Course>().eq(Course::getParentId,-1));
        //将所有父课程放入tree,并获取封面url
        List<CourseTreeDto> tree = list.stream().map((course) -> {
            return new CourseTreeDto(course, null, resourceUrlUtil.getResourceUrlById(course.getResourceId()));
        }).toList();
        return Result.success(tree);
    }

    @GetMapping("/{parentId}/lessons")
    public Result<List<Course>> getLessons(@PathVariable("parentId") Integer parentId){
        List<Course> list = courseService.list(new LambdaQueryWrapper<Course>().eq(Course::getParentId,parentId));
        return Result.success(list);
    }

    @GetMapping("/video/{id}")
    public Result<String> getVideoUrl(@PathVariable("id") Integer id){
        Course course = courseService.getById(id);
        return Result.success(resourceUrlUtil.getResourceUrlById(course.getResourceId()));
    }
}

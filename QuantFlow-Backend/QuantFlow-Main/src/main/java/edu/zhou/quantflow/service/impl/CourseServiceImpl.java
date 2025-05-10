package edu.zhou.quantflow.service.impl;

import edu.zhou.quantflow.dto.CourseTreeDto;
import edu.zhou.quantflow.entity.Course;
import edu.zhou.quantflow.mapper.CourseMapper;
import edu.zhou.quantflow.service.ICourseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-13
 */
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {
}

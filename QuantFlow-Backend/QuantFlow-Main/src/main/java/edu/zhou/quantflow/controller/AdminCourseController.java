package edu.zhou.quantflow.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import edu.zhou.quantflow.dto.CourseTreeDto;
import edu.zhou.quantflow.entity.Course;
import edu.zhou.quantflow.entity.Resource;
import edu.zhou.quantflow.service.ICourseService;
import edu.zhou.quantflow.service.IResourceService;
import edu.zhou.quantflow.util.HashUtil;
import edu.zhou.quantflow.util.ResourceUrlUtil;
import edu.zhou.quantflow.util.Result;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.util.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-13
 */
@RestController
@RequestMapping("/admin/course")
@Log4j2
public class AdminCourseController {
    private MinioClient minioClient;
    private IResourceService resourceService;
    private ICourseService courseService;

    private ResourceUrlUtil resourceUrlUtil;


    @Autowired
    public void setMinioClient(MinioClient minioClient){
        this.minioClient = minioClient;
    }
    @Autowired
    public void setResourceService(IResourceService resourceService){
        this.resourceService = resourceService;
    }
    @Autowired
    public void setCourseService(ICourseService courseService){ this.courseService = courseService;}
    @Autowired
    public void setResourceUrlUtil(ResourceUrlUtil resourceUrlUtil){this.resourceUrlUtil = resourceUrlUtil;}


    @PostMapping("/uploadCover")
    public Result<?> uploadCover(@RequestPart("cover") MultipartFile cover) {
        if (cover.isEmpty()) {
            return Result.error("文件为空,上传失败");
        }

        String fileName = cover.getOriginalFilename();
        long fileSize = cover.getSize();
        if(fileSize>10*1000*1000) return Result.error("图片过大!不能超过10MB!");
        log.info("文件名: " + fileName + " 大小:" + fileSize);
        try {
            byte[] bytes = cover.getBytes();
            String hash = HashUtil.getHash12(new ByteArrayInputStream(bytes));
            Resource existingCover;
            if(  (existingCover = resourceService
                    .getOne(new LambdaQueryWrapper<Resource>().eq(Resource::getHash,hash))) !=null ){
                //已存在
                String pathInMinio = existingCover.getPath()+existingCover.getName();
                String coverUrl =  minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket("resources")
                        .object(pathInMinio)
                        .build());
                Map<String,Object> map = new HashMap<>();
                map.put("url",coverUrl);
                map.put("id",existingCover.getId());
                return Result.success(map);
            }
            else{
                Resource newResource = new Resource();
                newResource.setName(fileName);
                newResource.setHash(hash);
                newResource.setPath("image/cover/");
                newResource.setStatus("pending");


                String pathInMinio = newResource.getPath() + newResource.getName();
                minioClient.putObject(PutObjectArgs.builder()
                        .bucket("resources")
                        .contentType(cover.getContentType())
                        .object(pathInMinio)
                        .stream(new ByteArrayInputStream(bytes),fileSize,-1)
                        .build());
                resourceService.save(newResource);
                String coverUrl =  minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket("resources")
                        .object(pathInMinio)
                        .build());
                int id  = resourceService.getOne(new LambdaQueryWrapper<Resource>().eq(Resource::getHash,hash)).getId();
                Map<String,Object> result = new HashMap<>();
                result.put("url",coverUrl);
                result.put("id",id);
                return Result.success(result);
            }

        }catch (Exception e){
            log.error(e);
            return Result.error(e.getMessage());
        }

    }

    @PostMapping("/test")
    public Result<String> test(){
        return Result.success("success");
    }

    @PostMapping("/add")
    public Result<?> addCourse(@RequestBody Course course){
        course.setParentId(-1);
        courseService.save(course);
        return Result.success("新增成功");
    }
    @PostMapping("/addLesson")
    public Result<?> addLesson(@RequestBody Course course){
        resourceService.update(new LambdaUpdateWrapper<Resource>().eq(Resource::getId,course.getResourceId()).set(Resource::getStatus,"linked"));
        courseService.save(course);
        return Result.success("添加成功");
    }
    @PostMapping("/uploadVideo")
    public Result<?> uploadVideo(@RequestPart("video") MultipartFile file,@RequestPart("hash") String hash) throws Exception {
        Resource curResource;
        if((curResource = resourceService.getOne(new LambdaQueryWrapper<Resource>().eq(Resource::getHash,hash)))!=null){
            return Result.success(curResource.getId());
        }else {
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            if(fileSize>100*1000*1000) return Result.error("视频过大!不能超过100MB!");
            log.info("文件名: " + fileName + " 大小:" + fileSize);
            String pathInMinio = "video/"+fileName;
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("resources")
                    .stream(file.getInputStream(),fileSize,-1)
                    .object(pathInMinio).build()
            );
            curResource = new Resource();
            curResource.setStatus("pending");
            curResource.setName(fileName);
            curResource.setHash(hash);
            curResource.setPath("video/");
            resourceService.save(curResource);
            Integer id = resourceService.getOne(new LambdaQueryWrapper<Resource>().eq(Resource::getHash, hash)).getId();
            return Result.success(id);
        }
    }

    @GetMapping("/list")
    public Result<List<CourseTreeDto>> getCourseList() {
        List<Course> list = courseService.list();
        Map<Integer,CourseTreeDto> map = new HashMap<>();
        //将所有父课程放入map,并获取封面url
        list.stream().filter((course)-> course.getParentId() == -1).forEach( (course)->{
                CourseTreeDto courseTreeDto = new CourseTreeDto(course, new ArrayList<>(), resourceUrlUtil.getResourceUrlById(course.getResourceId()));
                map.put(course.getId(),courseTreeDto);
        });
        //将所有子课程放入父课程
        list.stream().filter((course)-> course.getParentId() != -1).forEach((course)->{
                map.get(course.getParentId()).getLessons().add(course);
        });
        List<CourseTreeDto> tree = map.values().stream().toList();
        return Result.success(tree);
    }
    @GetMapping("/{parentId}/lessons")
    public Result<List<Course>> getLessons(@PathVariable("parentId") Integer parentId){
        List<Course> list = courseService.list(new LambdaQueryWrapper<Course>().eq(Course::getParentId,parentId));
        return Result.success(list);
    }

    @PutMapping("/edit/{id}")
    public Result<?> editCourse(@PathVariable("id") Integer id,@RequestBody Course course){
        course.setId(id);
        courseService.updateById(course);
        return Result.success("修改成功");
    }
}

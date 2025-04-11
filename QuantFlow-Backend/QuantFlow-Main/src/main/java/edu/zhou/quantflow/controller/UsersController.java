package edu.zhou.quantflow.controller;

import edu.zhou.quantflow.dto.UsersDto;
import edu.zhou.quantflow.entity.MyUserDetails;
import edu.zhou.quantflow.entity.Users;
import edu.zhou.quantflow.service.IUsersService;
import edu.zhou.quantflow.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Zhouyue
 * @since 2025-03-05
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    private IUsersService usersService;
    private AuthenticationManager authenticationManager;

    @Autowired
    public void setUsersService(IUsersService usersService) {
        this.usersService = usersService;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager manager){
        this.authenticationManager= manager;
    }


    @PostMapping("/list")
    public Result<?> list() {
        return Result.success(usersService.list());
    }
    @PostMapping("/register")
    public Result<?> register(@RequestBody Users user) {
        boolean success =usersService.register(user);
        System.out.println(user.toString());
        if(success){
            return Result.success();
        }else return Result.error("用户名已被注册");
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Users user, HttpServletRequest request){
        try {

        //创建认证令牌
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword());
        //执行认证流程
        Authentication authentication = authenticationManager.authenticate(token);
        System.out.println("Authorities: "+authentication.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        HttpSession session =  request.getSession(true); //获取session, 没有就创建; SpringSecurity自动管理
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,context);

        Object principal = authentication.getPrincipal();
        if(principal instanceof MyUserDetails details){
            UsersDto dto = new UsersDto();
            dto.setId(details.getId());
            dto.setUsername(details.getUsername());
            dto.setEmail(details.getEmail());
            if(details.getUsername().equals("admin")) dto.setRole("admin");
            else dto.setRole("user");

            //System.out.println(details.getAuthorities().toString());
            return Result.success(dto);
        }
            return Result.error("获取用户登录信息失败!");

        }catch (Exception e){
            return Result.error("登录失败! "+e.getMessage());
        }
    }


}

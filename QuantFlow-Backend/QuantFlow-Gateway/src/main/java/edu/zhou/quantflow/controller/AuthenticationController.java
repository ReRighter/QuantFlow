package edu.zhou.quantflow.controller;


import edu.zhou.quantflow.entity.AuthRequest;
import edu.zhou.quantflow.entity.MyUserDetails;
import edu.zhou.quantflow.entity.Users;
import edu.zhou.quantflow.entity.UsersDto;
import edu.zhou.quantflow.util.Result;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @Author Righter
 * @Description
 * @Date since 4/11/2025
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    /*private AuthenticationManager authenticationManager;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager manager){
        this.authenticationManager= manager;
    }*/

    /*@PostMapping("/login")
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
    }*/

    private final ReactiveAuthenticationManager authenticationManager;
    private final ReactiveRedisOperations<Object,Object> reactiveRedisOperations;
    @Autowired
    public AuthenticationController(ReactiveAuthenticationManager authenticationManager,@Qualifier("reactiveRedisTemplate") ReactiveRedisOperations<Object,Object> reactiveRedisOperations){
        this.authenticationManager= authenticationManager;
        this.reactiveRedisOperations = reactiveRedisOperations;
    }
    @PostMapping("/login")
    public Mono<Result<UsersDto>> login(@RequestBody AuthRequest authRequest/*, ServerWebExchange exchange, Principal principal*/){
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword()))
                .map(auth->{
                    //拿到用户信息返回前端
                    UsersDto principal = (UsersDto) auth.getPrincipal();
                    System.out.println("userinfo:"+principal.toString());
                    return Result.success(principal);
                    //认证信息会自动存入SecurityContext后存入session
                })
                .onErrorResume(e-> Mono.just(new Result<UsersDto>(200,"登录失败:"+e.getMessage(),null)));

    }

    @GetMapping("/heath")
    public Mono<Result<String>> healthCheck(){
        return Mono.just(Result.success("ok"));
    }
    @GetMapping("/private")
    public Result<String> privateTest(){
        return Result.success("private");
    }
}

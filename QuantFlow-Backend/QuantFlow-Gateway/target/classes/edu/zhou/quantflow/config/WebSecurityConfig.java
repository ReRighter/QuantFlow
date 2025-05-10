package edu.zhou.quantflow.config;


import edu.zhou.quantflow.entity.UsersDto;
import edu.zhou.quantflow.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @Author Righter
 * @Description
 * @Date since 3/7/2025
 */
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {

    /*private SecurityProperties securityProperties;
    @Autowired
    public void setSecurityProperties(SecurityProperties s){
        this.securityProperties =s;
    }*/
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,CorsConfigurationSource corsConfigurationSource){
        http
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource))
                .csrf(ServerHttpSecurity.CsrfSpec::disable) //禁用csrf
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/auth/login","/main/users/**").permitAll() //登录注册等接口放行,登录放在网关,其他服务放在main
                        //.requestMatchers("/admin/**").hasRole("ADMIN")
                        .pathMatchers("/auth/private").hasAuthority("TEST")
                        //.requestMatchers("/test/test2").permitAll()
                        .anyExchange().permitAll() //.authenticated() //其他请求需认证
                )
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable) //禁用表单登录
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable) //禁用basic认证
                ;
        return http.build();
    }

    /*@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())  //启用cors, 允许前端访问
            .csrf(AbstractHttpConfigurer::disable) //禁用csrf
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/login","/main/users/**").permitAll() //登录注册等接口放行,登录放在网关,其他服务放在main
                //.requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/private").hasAuthority("TEST")
                //.requestMatchers("/test/test2").permitAll()
                .anyRequest().permitAll() //.authenticated() //其他请求需认证
        )
        .formLogin(AbstractHttpConfigurer::disable) //禁用表单登录
        .httpBasic(AbstractHttpConfigurer::disable) //禁用basic认证

        .sessionManagement(session->
                session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS) //启用session记录登录状态
                        .maximumSessions(1)// 单用户登录
                        .maxSessionsPreventsLogin(true)
        );

        return http.build();
    }*/

    @Bean
    public CorsConfigurationSource corsConfigurationSource(SecurityProperties s){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(s.getAllowedOrigins());//允许的源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    /*@Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }*/

    @Bean
    public ReactiveAuthenticationManager authenticationManager(UserRepository userRepository,PasswordEncoder passwordEncoder){
        return (authentication)->{
            String userName = authentication.getName();
            String password = authentication.getCredentials().toString();
            return userRepository.findByUsername(userName)
                    .switchIfEmpty(Mono.error(new UsernameNotFoundException("用户不存在")))
                    .flatMap(users -> {
                        if(!passwordEncoder.matches(password,users.getPassword())){
                            return Mono.error(new UsernameNotFoundException("密码错误"));
                        }
                        UsersDto principal = new UsersDto(null,users.getEmail(),users.getUsername(),users.getId());
                            Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
                            if(userName.equals("admin")) {
                                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                                principal.setRole("admin");
                            } //管理员特权
                            else principal.setRole("user");

                            authorities.add(new SimpleGrantedAuthority("TEST"));

                        return  Mono.just(new UsernamePasswordAuthenticationToken(principal,null,authorities));
                    });
        };

    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

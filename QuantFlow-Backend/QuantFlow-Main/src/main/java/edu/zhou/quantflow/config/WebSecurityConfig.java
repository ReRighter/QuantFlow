package edu.zhou.quantflow.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @Author Righter
 * @Description
 * @Date since 3/7/2025
 */
@Configuration
public class WebSecurityConfig {

    private SecurityProperties securityProperties;
    @Autowired
    public void setSecurityProperties(SecurityProperties s){
        this. securityProperties =s;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())  //启用cors, 允许前端访问
            .csrf(AbstractHttpConfigurer::disable) //禁用csrf
            .authorizeHttpRequests(authorize -> authorize
                //.requestMatchers("/users/**").permitAll() //用户接口放行
                //.requestMatchers("/admin/**").hasRole("ADMIN")
                //.requestMatchers("/test/test1").hasAuthority("TEST")
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
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(securityProperties.getAllowedOrigins());//允许的源
        //System.out.println("源 "+ securityProperties.getAllowedOrigins().get(0));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

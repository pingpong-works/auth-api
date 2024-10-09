package com.auth.config;


import com.auth.auth.filter.JwtAuthenticationFilter;
import com.auth.auth.filter.JwtVerificationFilter;
import com.auth.auth.handler.EmployeeAccessDeniedHandler;
import com.auth.auth.handler.EmployeeAuthenticationEntryPoint;
import com.auth.auth.handler.EmployeeAuthenticationFailureHandler;
import com.auth.auth.handler.EmployeeAuthenticationSuccessHandler;
import com.auth.auth.jwt.JwtTokenizer;
import com.auth.auth.utils.JwtAuthorityUtils;
import com.auth.employee.repository.EmployeeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    private final JwtTokenizer jwtTokenizer;
    private final JwtAuthorityUtils jwtAuthorityUtils;
    private final EmployeeRepository employeeRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public SecurityConfig(JwtTokenizer jwtTokenizer, JwtAuthorityUtils jwtAuthorityUtils, EmployeeRepository employeeRepository, RedisTemplate<String, Object> redisTemplate) {
        this.jwtTokenizer = jwtTokenizer;
        this.jwtAuthorityUtils = jwtAuthorityUtils;
        this.employeeRepository = employeeRepository;
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .cors()
                .and()
                .csrf().disable()
                .logout().disable()  // 직접 구현한 로그아웃 사용
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .exceptionHandling()
                .authenticationEntryPoint(new EmployeeAuthenticationEntryPoint())
                .accessDeniedHandler(new EmployeeAccessDeniedHandler())
                .and()
                .apply(new CustomFilterConfigurer())
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // OPTIONS 요청 허용
                        .antMatchers(HttpMethod.POST, "/logout").permitAll()
                        .anyRequest().permitAll() // 모든 요청을 허용 (추가 보안 필요 시 제한 가능)
                );
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        // 로그인 성공시 memberId 를 header 에 전달할 수도 있지만? 다른 방법이 더 낫다고.. 다른 방법을 생각해 봅세.
//        configuration.setExposedHeaders(Arrays.asList("Authorization", "Location"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));  // 모든 오리진 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE", "OPTIONS"));  // 허용하는 HTTP 메서드 설정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "X-Requested-With", "Accept", "content-type"));  // 허용되는 헤더g
        configuration.setExposedHeaders(Arrays.asList("Authorization", "employeeId"));  // 노출할 헤더 추가
        configuration.setAllowCredentials(true);  // 인증 관련 정보를 허용
        configuration.addAllowedOriginPattern("*");  // 모든 Origin 허용 (테스트 목적)


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); //모든 경로에 대해 CORS 허용
        return source;
    }

    public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
        @Override
        public void configure (HttpSecurity builder) throws Exception {
            AuthenticationManager authenticationManager =
                    builder.getSharedObject(AuthenticationManager.class);
            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtTokenizer, employeeRepository, passwordEncoder());
            jwtAuthenticationFilter.setFilterProcessesUrl("/login");
            jwtAuthenticationFilter.setAuthenticationSuccessHandler(new EmployeeAuthenticationSuccessHandler());
            jwtAuthenticationFilter.setAuthenticationFailureHandler(new EmployeeAuthenticationFailureHandler());
            JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(jwtTokenizer, jwtAuthorityUtils, redisTemplate);


            builder.addFilter(jwtAuthenticationFilter)
                    .addFilterAfter(jwtVerificationFilter, JwtAuthenticationFilter.class);
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

}
package com.kkultrip.threego.config.security;

import com.kkultrip.threego.config.security.auth.UserDetailsServiceImpl;
import com.kkultrip.threego.config.security.jwt.AccessDeniedJwt;
import com.kkultrip.threego.config.security.jwt.AuthEntryPointJwt;
import com.kkultrip.threego.config.security.jwt.AuthTokenFilter;
import com.kkultrip.threego.config.security.jwt.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(1)
public class SecurityApiConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsServiceImpl userDetailsService;
    private final CorsConfig corsConfig;
    private final AuthEntryPointJwt authEntryPointJwt;
    private final AccessDeniedJwt accessDeniedJwt;
    private final JwtUtils jwtutils;

    public SecurityApiConfig(UserDetailsServiceImpl userDetailsService, CorsConfig corsConfig, AuthEntryPointJwt authEntryPointJwt, AccessDeniedJwt accessDeniedJwt, JwtUtils jwtutils) {
        this.userDetailsService = userDetailsService;
        this.corsConfig = corsConfig;
        this.authEntryPointJwt = authEntryPointJwt;
        this.accessDeniedJwt = accessDeniedJwt;
        this.jwtutils = jwtutils;
    }

    @Bean
    public AuthTokenFilter authTokenFilter(){
        return new AuthTokenFilter(jwtutils, userDetailsService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // cors ?????? ??????
                .addFilter(corsConfig.corsFilter())
                // csrf ????????????
                .csrf().disable()

                // ????????????
                .addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class)

                //????????????
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPointJwt)
                .accessDeniedHandler(accessDeniedJwt)
                .and()
                // ?????? ?????? ??????
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // ????????? ?????? ??????
                .and()
                .antMatcher("/api/**")
                .authorizeRequests().antMatchers("/api/**").permitAll()
                .anyRequest().authenticated();


    }
}

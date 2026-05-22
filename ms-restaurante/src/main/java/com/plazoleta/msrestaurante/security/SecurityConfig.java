package com.plazoleta.msrestaurante.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtils);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(auth -> auth
                        .antMatchers("/actuator/health", "/actuator/info").permitAll()
                        .antMatchers("/restaurantes", "/restaurantes/*/platos").permitAll()
                        .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .antMatchers("/internal/**").permitAll()
                        .antMatchers(org.springframework.http.HttpMethod.POST, "/propietarios").hasRole("ADMIN")
                        .antMatchers(org.springframework.http.HttpMethod.POST, "/restaurantes").hasRole("ADMIN")
                        .antMatchers(org.springframework.http.HttpMethod.POST, "/restaurantes/*/empleados").hasRole("PROPIETARIO")
                        .antMatchers(org.springframework.http.HttpMethod.POST, "/restaurantes/*/platos").hasRole("PROPIETARIO")
                        .antMatchers(org.springframework.http.HttpMethod.PUT, "/restaurantes/*/platos/**").hasRole("PROPIETARIO")
                        .antMatchers(org.springframework.http.HttpMethod.DELETE, "/restaurantes/*/platos/**").hasRole("PROPIETARIO")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

package org.exi.demo.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MySecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> {})

            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/h2-console/**","/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                //UTENTI
                .requestMatchers(HttpMethod.GET, "/api/utenti/**").hasAnyRole("USER", "ADMIN")

                //ADMIN
                .requestMatchers(HttpMethod.POST, "/api/utenti/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/utenti/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/utenti/**").hasRole("ADMIN")

                .anyRequest().authenticated()
            )

            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
    	
    	UserDetails admin = User.builder()
    							.username("admin")
    							.password(encoder.encode("admin1234"))
    							.roles("ADMIN")
    							.build();
    	
    	UserDetails user = User.builder()
    						   .username("user")
    						   .password(encoder.encode("user1234"))
    						   .roles("USER")
    						   .build();
    	
		return new InMemoryUserDetailsManager(admin, user);
    	
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}

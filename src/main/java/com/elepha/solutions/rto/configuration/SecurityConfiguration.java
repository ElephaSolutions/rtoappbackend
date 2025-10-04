package com.elepha.solutions.rto.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                authorize -> authorize
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
        ).securityContext(
                securityContext -> securityContext.requireExplicitSave(true)
        ).csrf(
                AbstractHttpConfigurer::disable
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public SecurityContextRepository delegatingSecurityContextRepository() {
        return new DelegatingSecurityContextRepository(new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = User.builder()
                .username("user")
                .password("{bcrypt}$2a$10$g6NPVdq6kaD/zVan22/aeuLGctzDBpD.e/64ZQT6Qxo7xxCo7UeG.")
                .roles("USER")
                .build();
        UserDetails adminDetails = User.builder()
                .username("admin")
                .password("{bcrypt}$2a$10$JLgRZFw.nGCo7.2YLY9YVezIz9xfnU7F5zzQCAHG4Lwx6jdlcRkzS")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(userDetails);
    }
}

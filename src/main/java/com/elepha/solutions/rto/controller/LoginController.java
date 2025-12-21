package com.elepha.solutions.rto.controller;

import com.elepha.solutions.rto.dto.SignUpRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    protected static final String INSERT_NEW_USER = "insert into users (username, password, enabled, owner_name, agency_name, contact_number) values (:username, :password, :enabled, :owner_name, :agency_name, :contact_number)";
    protected static final String INSERT_USER_GRANTS = "insert into authorities(username, authority) values (:username, :authority)";

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository httpSessionSecurityContextRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public LoginController(AuthenticationManager authenticationManager, SecurityContextRepository httpSessionSecurityContextRepository
            , NamedParameterJdbcTemplate namedParameterJdbcTemplate, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.httpSessionSecurityContextRepository = httpSessionSecurityContextRepository;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/api/login")
    public void login(@RequestBody LoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.username(), loginRequest.password());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        httpSessionSecurityContextRepository.saveContext(context, request, response);
    }

    @Transactional
    @PostMapping("/api/signUp")
    public ResponseEntity<Void> signUpUser (@RequestBody SignUpRequestDTO signUpRequestDTO) {
        MapSqlParameterSource sqlUserParameterSource = new MapSqlParameterSource();
        sqlUserParameterSource.addValue("username", signUpRequestDTO.username());
        sqlUserParameterSource.addValue("password", passwordEncoder.encode(signUpRequestDTO.password()));
        sqlUserParameterSource.addValue("enabled", true);
        sqlUserParameterSource.addValue("owner_name", signUpRequestDTO.ownerName());
        sqlUserParameterSource.addValue("agency_name", signUpRequestDTO.agencyName());
        sqlUserParameterSource.addValue("contact_number", signUpRequestDTO.contactNumber());
        namedParameterJdbcTemplate.update(INSERT_NEW_USER, sqlUserParameterSource);

        MapSqlParameterSource sqlAuthoritiesParameterSource = new MapSqlParameterSource();
        sqlAuthoritiesParameterSource.addValue("username", signUpRequestDTO.username());
        sqlAuthoritiesParameterSource.addValue("authority", "USER");
        namedParameterJdbcTemplate.update(INSERT_USER_GRANTS, sqlAuthoritiesParameterSource);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    public record LoginRequest(String username, String password) {}
}
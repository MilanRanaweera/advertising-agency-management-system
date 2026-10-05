package com.axiom.users;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Bean
  PasswordEncoder encoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService details(AppUserRepository repo) {
    return email -> {
      String login = email.trim().toLowerCase(java.util.Locale.ROOT);
      boolean adminAlias = login.equals("admin");
      var u = repo
        .findByEmail(adminAlias ? "admin@axiom.test" : login)
        .orElseThrow(() -> new UsernameNotFoundException("Invalid login"));
      if (adminAlias && !"ADMIN".equals(u.role)) throw new UsernameNotFoundException("Invalid login");
      return User.withUsername(u.email)
        .password(u.passwordHash)
        .roles(u.role)
        .disabled(!u.active || u.deleted)
        .build();
    };
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http
      .csrf(c -> c.disable())
      .sessionManagement(s ->
        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      )
      .authorizeHttpRequests(a ->
        a
          .requestMatchers(
            "/api/auth/register",
            "/api/catalog/public",
            "/error"
          )
          .permitAll()
          .anyRequest()
          .authenticated()
      )
      .httpBasic(b -> {})
      .build();
  }
}

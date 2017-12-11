package com.clumsy.gymbadger.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
 
@SpringBootApplication
@EnableOAuth2Sso
@ComponentScan(basePackages = "com.clumsy.gymbadger")
@EnableJpaRepositories(basePackages = "com.clumsy.gymbadger.repos")
@EntityScan(basePackages = "com.clumsy.gymbadger.entities")
public class App extends WebSecurityConfigurerAdapter {
 
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http
        .antMatcher("/**")
        .authorizeRequests()
          .antMatchers("/", "/login**", "/api/areas/**", "/api/defaults/**", "/api/gyms/**")
          .permitAll()
        .anyRequest()
          .authenticated()
        .and().logout().logoutSuccessUrl("/").permitAll()
        .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}

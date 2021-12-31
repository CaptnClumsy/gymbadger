package com.clumsy.gymbadger.app;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.clumsy.gymbadger.security.AppUserService;
import com.clumsy.gymbadger.security.CustomAuthorizationRequestResolver;
 
@SpringBootApplication
@ComponentScan(basePackages = "com.clumsy.gymbadger")
@EnableJpaRepositories(basePackages = "com.clumsy.gymbadger.repos")
@EntityScan(basePackages = "com.clumsy.gymbadger.entities")
public class App extends WebSecurityConfigurerAdapter {

	@Autowired
    private AppUserService oauth2UserService;

	@Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      // Force SSL
      http.requiresChannel().anyRequest().requiresSecure();
    	
      // Allow some URLs without authentication and enable CSRF
      http
        .authorizeRequests()
          .antMatchers("/", "/*", "/login**", "/api/areas/**", "/api/defaults/**", 
        		  "/api/users/leaderboard/**", "/api/gyms/**", "/api/bosses/**", "/api/comments/**", 
        		  "/tos.html", "/api/upload/**", "/api/regions/**", "/api/cells/**",
        		  "/css/*", "/js/*", "/images/**")
          .permitAll()
        .anyRequest()
          .authenticated()
        .and().logout().logoutSuccessUrl("/").permitAll()
        .and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        .and()
          .oauth2Login()
              .loginPage("/oauth2/authorization/facebook")
              .userInfoEndpoint()
                .userService(oauth2UserService)
              .and()
              .authorizationEndpoint()
                .authorizationRequestResolver(
                      new CustomAuthorizationRequestResolver(
                              this.clientRegistrationRepository))    
	          .and()
	          .successHandler(new AuthenticationSuccessHandler() {
	              @Override
	              public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
	                    Authentication authentication) throws IOException, ServletException {
	                HttpSession session = request.getSession(false);
	                if (session!=null) {
	                	Long gymId = (Long)session.getAttribute("gymId");
	                	if (gymId!=null && gymId!=0L) {
	                		response.sendRedirect("/?gymid=" + gymId);
	                	    return;
	                	}
	                	String regionName = (String)session.getAttribute("regionName");
	                	if (regionName!=null && !regionName.equals("none")) {
	                	    response.sendRedirect("/" + regionName);
	                	    return;
	                	}
	                }
	                response.sendRedirect("/");
	              }
	          });
        
        ;
    }
}

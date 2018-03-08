package ch.fhnw.wodss.tippspiel.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RESTAuthenticationSuccessHandler restAuthenticationSuccessHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception{
        return super.authenticationManager();
    }

    @Bean
    public RESTAuthenticationFilter restAuthenticationFilter() {
        RESTAuthenticationFilter restAuthenticationFilter = new RESTAuthenticationFilter(objectMapper);
        restAuthenticationFilter.setAuthenticationManager(authenticationManager);
        restAuthenticationFilter.setAuthenticationSuccessHandler(restAuthenticationSuccessHandler);
        return restAuthenticationFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    // Todo move HTTPS settings to apache server
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and().anonymous().disable()
                .csrf().disable() // CSRF protection is done with custom HTTP header (OWASP suggestion)
                .addFilterBefore(new XRequestedWithHeaderFilter(), CsrfFilter.class)
                .addFilterBefore(new EnforceCorsFilter(), CsrfFilter.class)
                .addFilterBefore(restAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .logout().logoutSuccessHandler((request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
                .and()
                // Security Headers http://docs.spring.io/spring-security/site/docs/current/reference/html/headers.html
                .headers()
                .frameOptions().sameOrigin()
                .contentSecurityPolicy("default-src 'self'; script-src 'self' 'unsafe-inline'; report-uri /csp")
                // HSTS (you may consider setting this header in the ssl handling part of your app e.g. apache, nginix)
                .and()
                // be careful when deploying this 2 years policy because it will prevent your customers browsers from visiting your page without ssl
                .httpStrictTransportSecurity()
                .maxAgeInSeconds(63072000);
        // Provide a logout route to clear the session
        http
                .logout()
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("BettingGame_SchranerOhmeZumbrunn_JSESSIONID");

        // Use session fixation for secure HTTP to HTTPS rewrite
        // NOTE: https://en.wikipedia.org/wiki/Session_fixation
        http
                .sessionManagement()
                .sessionFixation()
                .newSession();
    }
}

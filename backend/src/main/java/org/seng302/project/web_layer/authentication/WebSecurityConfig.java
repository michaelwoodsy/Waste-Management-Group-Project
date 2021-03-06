package org.seng302.project.web_layer.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

/**
 * Class for configuring application web security.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Bean which reveals authentication manager to rest of app.
     *
     * @return authentication manager.
     * @throws Exception exception.
     */
    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Bean which provides user detail service for getting user details.
     *
     * @return new AppUserDetailsService object.
     */
    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        return new AppUserDetailsService();
    }

    /**
     * Bean which provides password encoder.
     *
     * @return new BCryptPasswordEncoder object.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates and returns the authentication provider to be used when authenticating user credentials.
     * This authentication provider will retrieve user details from the database through the user details service.
     *
     * @return the authentication provider used by the authentication manager.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configuration for authentication manager.
     *
     * @param auth authentication manager builder.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * Configures security for HTTP.
     *
     * @param http HTTP security configuration.
     * @throws Exception exception.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/login", "/users", "/lostpassword/send").permitAll()
                .antMatchers(HttpMethod.GET, "/lostpassword/validate", "/statistics", "/popularlistings").permitAll()
                .regexMatchers(HttpMethod.GET, "\\/media\\/(.*)").permitAll()
                .antMatchers(HttpMethod.PATCH, "/lostpassword/edit").permitAll()
                .antMatchers(HttpMethod.POST, "/contact").permitAll()
                .anyRequest().authenticated();
        http.requestCache().requestCache(new NullRequestCache());
        http.cors();
        http.csrf().disable();
        http.logout() //Can call '/logout' to log out
                .permitAll()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
        http.httpBasic().authenticationEntryPoint(new NoPopupBasicAuthenticationEntryPoint());
        http.formLogin().disable();
    }

}

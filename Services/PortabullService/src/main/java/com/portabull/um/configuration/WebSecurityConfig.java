package com.portabull.um.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService myUserDetailsService;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.csrf().disable().authorizeRequests().antMatchers("/**").permitAll().anyRequest()
//                .authenticated().and().exceptionHandling().and().sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);


        httpSecurity.authorizeRequests()
                .mvcMatchers("/sign-in-with-gmail").authenticated()
                .and().logout().invalidateHttpSession(true)
                .clearAuthentication(true).logoutSuccessUrl("/plogout").deleteCookies("JSESSIONID")
                .permitAll().and().csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());


        httpSecurity.oauth2Login();

        httpSecurity.csrf().disable().authorizeRequests().antMatchers("/**").permitAll();

    }

}

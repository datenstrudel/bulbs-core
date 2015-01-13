package net.datenstrudel.bulbs.core.security.config;

import net.datenstrudel.bulbs.core.application.services.BulbsContextUserService;
import net.datenstrudel.bulbs.core.web.filter.PreAuthenticationProcessingFilter;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig {
    
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Configuration
    @Profile(value = "development")
    @EnableWebSecurity
    @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
    public static class WebSecurityConfigDevel extends WebSecurityConfigurerAdapter {
        //~ Member(s) //////////////////////////////////////////////////////////////
        @Autowired
        BulbsContextUserService userService;
        @Autowired
        PasswordEncoder passwordEncoder;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    .antMatchers("/assets/**")
                    .antMatchers("/bulbsCore/**")
                    .antMatchers("/css/**")
                    .antMatchers("/js/**")
            ;
        }
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            log.info("Init Security HTTP authorizations..");
            http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/assets/**").permitAll()
                    .antMatchers("/core/identity/signIn/**").permitAll()
                    .antMatchers("/core/identity/signUp/**").permitAll()
                    .antMatchers("/core/websockets/info/**").permitAll()
                    .antMatchers("/core/websockets/**").authenticated()
                    .antMatchers("/core/bridges/**").authenticated()
                    .antMatchers("/core/bulbs/**").authenticated()
                    .antMatchers("/core/groups/**").authenticated()
                    .antMatchers("/core/presets/**").authenticated()
                    .antMatchers("/core/schedules/**").authenticated()
                    .antMatchers("/manage/**").authenticated()
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .logout()
                    .logoutSuccessUrl("/")
                    .logoutUrl("/logout")
                    .permitAll()
                    .and()
                .formLogin()
                    .loginPage("/")
                    .loginProcessingUrl("/login")
                    .failureUrl("/")
                    .failureHandler(new AuthenticationFailureHandler() {
                        @Override
                        public void onAuthenticationFailure(HttpServletRequest request,
                                HttpServletResponse response, AuthenticationException exception)
                                throws IOException, ServletException {
                            response.getWriter().append("Bad credentials!");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                        }
                    })

                    .passwordParameter("password")
                    .usernameParameter("email")
                    .and()
                .rememberMe()
                    .userDetailsService(userService)
                    .and()
                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                ;
            /* Allows authentication by API-KEY present in request header "Auth".
              */
            PreAuthenticationProcessingFilter preAuthFilter = new PreAuthenticationProcessingFilter();
            preAuthFilter.setAuthenticationManager(authenticationManagerBean());
            http.addFilterBefore(preAuthFilter, AbstractPreAuthenticatedProcessingFilter.class);

    //        http.sessionManagement().
        }
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            PreAuthenticatedAuthenticationProvider preAuthProvider =
                    new PreAuthenticatedAuthenticationProvider();
            preAuthProvider.setPreAuthenticatedUserDetailsService(userService);

            DaoAuthenticationProvider usernameDetailAuthProvider = new DaoAuthenticationProvider();
            usernameDetailAuthProvider.setUserDetailsService(userService);
            usernameDetailAuthProvider.setPasswordEncoder(passwordEncoder);

            // Actually used to authenticate requests by http header attribute 'Auth'
            auth.authenticationProvider(preAuthProvider)
                .authenticationProvider(usernameDetailAuthProvider);

        }

        @Bean
        public AuthenticationManager coreAuthenticationManager() throws Exception {
            return authenticationManagerBean();
        }
    }
        
    @Configuration
    @Profile(value = "production")
    @EnableWebSecurity
    @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
    public static class WebSecurityConfigProduction extends WebSecurityConfigurerAdapter {
        //~ Member(s) //////////////////////////////////////////////////////////////
        @Autowired
        BulbsContextUserService userService;
        @Autowired
        PasswordEncoder passwordEncoder;
        
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
                    .antMatchers("/assets/**")
                    .antMatchers("/bulbsCore/**")
                    .antMatchers("/css/**")
                    .antMatchers("/js/**")
            ;
        }
        @Override 
        protected void configure(HttpSecurity http) throws Exception {
            log.info("Init Security HTTP authorizations..");
            http
                .csrf().disable()
                .authorizeRequests()
                    .antMatchers("/assets/**").permitAll()
                    .antMatchers("/core/identity/signIn/**").permitAll()
                    .antMatchers("/core/identity/signUp/**").permitAll()
                    .antMatchers("/core/websockets/**").authenticated()
                    .antMatchers("/core/websockets/info/**").permitAll()
                    .antMatchers("/core/bridges/**").authenticated()
                    .antMatchers("/core/bulbs/**").authenticated()
                    .antMatchers("/core/groups/**").authenticated()
                    .antMatchers("/core/presets/**").authenticated()
                    .antMatchers("/core/schedules/**").authenticated()
                    .antMatchers("/manage/**").authenticated()
                    .antMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .logout()
                    .logoutSuccessUrl("/")
                    .logoutUrl("/logout")
                    .permitAll()
                    .and()
                .formLogin()
                    
                    .loginPage("/")
                    .loginProcessingUrl("/login")
                    .failureUrl("/")
                    .failureHandler(new AuthenticationFailureHandler() {
                        @Override
                        public void onAuthenticationFailure(HttpServletRequest request, 
                                HttpServletResponse response, AuthenticationException exception) 
                                throws IOException, ServletException {
                            response.getWriter().append("Bad credentials!");
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                        }
                    })

                    .passwordParameter("password")
                    .usernameParameter("email")
                    .and()
                .rememberMe()
                    .userDetailsService(userService)
                    .and()
                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                    .and()
                .requiresChannel().anyRequest().requiresSecure()
                ;
            /* Allows authentication by API-KEY present in request header "Auth".
              */
            PreAuthenticationProcessingFilter preAuthFilter = new PreAuthenticationProcessingFilter();
            preAuthFilter.setAuthenticationManager(authenticationManagerBean());
            http.addFilterBefore(preAuthFilter, AbstractPreAuthenticatedProcessingFilter.class);

    //        http.sessionManagement().
        }
        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            PreAuthenticatedAuthenticationProvider preAuthProvider = 
                    new PreAuthenticatedAuthenticationProvider();
            preAuthProvider.setPreAuthenticatedUserDetailsService(userService);

            DaoAuthenticationProvider usernameDetailAuthProvider = new DaoAuthenticationProvider();
            usernameDetailAuthProvider.setUserDetailsService(userService);
            usernameDetailAuthProvider.setPasswordEncoder(passwordEncoder);
            
            // Actually used to authenticate requests by http header attribute ApiKey
            auth.authenticationProvider(preAuthProvider)
                .authenticationProvider(usernameDetailAuthProvider);
            
        }

        @Bean
        public AuthenticationManager coreAuthenticationManager() throws Exception {
            return authenticationManagerBean();
        }
    }
    //~ Private Artifact(s) ////////////////////////////////////////////////////

}

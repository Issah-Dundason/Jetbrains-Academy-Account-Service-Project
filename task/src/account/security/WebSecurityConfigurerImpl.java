package account.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfigurerImpl extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userService;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final RestAuthenticationEntryPoint entryPoint;

    @Autowired
    public WebSecurityConfigurerImpl(UserDetailsService userService, RestAccessDeniedHandler accessDeniedHandler, RestAuthenticationEntryPoint entryPoint) {
        this.userService = userService;
        this.accessDeniedHandler = accessDeniedHandler;
        this.entryPoint = entryPoint;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.userDetailsService(userService).passwordEncoder(getEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .and()
                .authorizeRequests()
                .mvcMatchers("api/admin/**")
                .hasAnyRole("ADMINISTRATOR")
                .mvcMatchers("api/acct/**")
                .hasAnyRole("ACCOUNTANT")
                .mvcMatchers("api/security/events")
                .hasAnyRole("AUDITOR")
                .mvcMatchers("api/empl/**")
                .hasAnyRole("ACCOUNTANT", "USER")
                .mvcMatchers("api/auth/changepass")
                .authenticated()
                .anyRequest()
                .permitAll();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.httpBasic().authenticationEntryPoint(entryPoint);
        http.csrf().disable().headers().frameOptions().disable();
    }


    @Bean
    public PasswordEncoder getEncoder() {
        return new BCryptPasswordEncoder(13);
    }
}

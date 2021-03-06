package config.security;

import authentication.DBAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableTransactionManagement(proxyTargetClass = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    DBAuthenticationService dbAuthenticationService;

    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(dbAuthenticationService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable();
        httpSecurity.authorizeRequests().antMatchers("/", "/login", "/logout", "/register", "/book")
                .permitAll();
        httpSecurity.authorizeRequests().antMatchers("/account", "/cart", "/placeOrder",
                "/deleteAccount", "/cancelOrder", "/cancelOrder/*", "/addToCart", "/addToCart/*",
                "/deleteFromCart", "/deleteFromCart/*", "/clearCart")
                .access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')");
        httpSecurity.authorizeRequests().antMatchers("/addBook", "/addItem", "/editBook",
                "/orderList", "/order", "/removeItem", "/deleteBook",
                "/deleteBook/*")
                .access("hasRole('ROLE_ADMIN')");
        httpSecurity.authorizeRequests().and().formLogin()
                .loginProcessingUrl("/j_spring_security_check")
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .usernameParameter("eMail")
                .passwordParameter("password")
                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/");
    }
}
